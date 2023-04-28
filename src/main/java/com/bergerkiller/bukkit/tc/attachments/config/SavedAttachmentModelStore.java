package com.bergerkiller.bukkit.tc.attachments.config;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentTypeRegistry;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentItem;
import com.bergerkiller.bukkit.tc.attachments.ui.AttachmentEditor;
import com.bergerkiller.bukkit.tc.exception.IllegalNameException;
import com.bergerkiller.bukkit.tc.properties.SavedClaim;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.BasicModularConfiguration;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationEntry;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationFile;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ModularConfigurationModule;
import com.bergerkiller.bukkit.tc.utils.modularconfiguration.ReadOnlyModuleException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import static com.bergerkiller.bukkit.common.utils.MaterialUtil.getMaterial;

/**
 * Stores the attachment configurations for models that have been saved.
 * These models can be used in the MODEL attachment or pasted into
 * the attachment editor.
 */
public abstract class SavedAttachmentModelStore implements TrainCarts.Provider {
    static final String KEY_SAVED_NAME = "savedName";

    protected final TrainCarts traincarts;
    protected final ModularConfigurationEntry.Container<SavedAttachmentModel> container;

    protected SavedAttachmentModelStore(TrainCarts traincarts,
                                        ModularConfigurationEntry.Container<SavedAttachmentModel> container)
    {
        this.traincarts = traincarts;
        this.container = container;
    }

    /**
     * Creates and initializes a new SavedAttachmentModelStore
     *
     * @param traincarts Main TrainCarts plugin instance
     * @param filename Main SavedModels.yml file path
     * @param directoryName SavedModelModules directory path
     * @return new store
     */
    public static SavedAttachmentModelStore create(TrainCarts traincarts, String filename, String directoryName) {
        ModularConfig modularConfig = new ModularConfig(traincarts, filename, directoryName);
        return new DefaultStore(traincarts, modularConfig);
    }

    static SavedAttachmentModelStore createModule(ModularConfigurationModule<SavedAttachmentModel> module) {
        ModularConfig modularConfig = (ModularConfig) module.getMain();
        if (module == modularConfig.getDefaultModule()) {
            return modularConfig.traincarts.getSavedAttachmentModels();
        } else {
            return new ModuleStore(modularConfig.traincarts, module);
        }
    }

    @Override
    public TrainCarts getTrainCarts() {
        return traincarts;
    }

    /**
     * Gets the name of this saved attachment model store. If this is a module,
     * returns the name of the module, otherwise returns null.
     * 
     * @return module name
     */
    public String getName() {
        return container.getName();
    }

    /**
     * Gets whether this store is the default module
     * 
     * @return True if this is the default module
     */
    public abstract boolean isDefault();

    /**
     * Gets a sorted list of modules that have been created
     * 
     * @return list of module names
     */
    public abstract List<String> getModuleNames();

    /**
     * Gets a module by name
     * 
     * @param moduleName
     * @return module, null if no module by this name exists
     */
    public abstract SavedAttachmentModelStore getModule(String moduleName);

    /**
     * Checks to see what module an attachment model is saved in.
     * If it is saved in the default place, null is returned.
     * 
     * @param name
     * @return module name, null if not stored in a separate module
     */
    public String getModuleNameOfModel(String name) {
        ModularConfigurationEntry<SavedAttachmentModel> entry = container.getIfExists(name);
        return entry == null ? null : entry.getModule().getName();
    }

    /**
     * Sets the module an attachment model is saved in.
     * If it is currently not saved in a module and a non-null module is provided,
     * the model will be moved to a module of that name. Similarly if a model was
     * saved in a module and a null module is provided, the model is moved out of the
     * module.<br>
     * <br>
     * If no saved attachment model with this name exists, this method does nothing.
     * Make sure to call this method after saving configuration, not before.
     * 
     * @param name
     * @param module name, null for the default store
     */
    public abstract void setModuleNameOfModel(String name, String module);

    /**
     * Checks whether a player has permission to make changes to a saved attachment model.
     * Returns true if no model by this name exists yet.
     * 
     * @param sender Player or Command Sender to check
     * @param name Permission name
     * @return True if the player has permission
     */
    public boolean hasPermission(CommandSender sender, String name) {
        SavedAttachmentModel savedProperties = this.getModel(name);
        return savedProperties == null || savedProperties.hasPermission(sender);
    }

    /**
     * Gets a list of players that have claimed ownership over a saved attachment model.
     * An empty list indicates nobody has claimed the saved attachment model, or that the saved
     * attachment model does not exist. The returned list is unmodifiable.
     * 
     * @param name
     * @return list of player claims, empty is unclaimed or non-existant model
     */
    public Set<SavedClaim> getClaims(String name) {
        SavedAttachmentModel savedProperties = this.getModel(name);
        return (savedProperties == null) ? Collections.emptySet() : savedProperties.getClaims();
    }

    /**
     * Calls {@link #setClaims(String, Collection)} with a single claim of a player.
     * 
     * @param name
     * @param player to add to the claim list
     */
    public void setClaim(String name, Player player) {
        setClaims(name, Collections.singleton(new SavedClaim(player)));
    }

    /**
     * Sets a list of players that have claimed ownership over a saved attachment model.
     * An empty list indicates nobody has claimed the saved attachment model.
     * Fails silently if the model does not exist.
     * 
     * @param name
     * @param claims list to set to
     */
    public void setClaims(String name, Collection<SavedClaim> claims) {
        SavedAttachmentModel savedModel = this.getModel(name);
        if (savedModel != null) {
            savedModel.setClaims(claims);
        }
    }

    /**
     * Checks whether a particular saved attachment model name exists inside this store
     * 
     * @param name
     * @return True if the model is contained
     */
    public boolean containsModel(String name) {
        return container.getIfExists(name) != null;
    }

    public abstract void save(boolean autosave);

    /**
     * Reloads saved attachment model configurations from disk
     */
    public abstract void reload();

    /**
     * Sets a default attachment configuration for a saved attachment model if
     * the model by this name does not yet exist.
     *
     * @param name of the saved attachment model
     * @return Existing or created saved attachment model
     * @throws IllegalNameException If the name is null or empty
     */
    public SavedAttachmentModel setDefaultConfigIfMissing(String name) throws IllegalNameException {
        SavedAttachmentModel existing = getModel(name);
        if (existing != null) {
            return existing;
        }

        ConfigurationNode config = new ConfigurationNode();
        AttachmentTypeRegistry.instance().toConfig(config, CartAttachmentItem.TYPE);
        config.set("item", new ItemStack(getMaterial("LEGACY_WOOD")));
        return setConfig(name, config);
    }

    /**
     * Sets the configuration for a saved attachment model
     * 
     * @param name of the saved attachment model
     * @param config to set to, is cloned before storing
     * @return Existing or created saved attachment model
     * @throws IllegalNameException If the name is null or empty
     */
    public SavedAttachmentModel setConfig(String name, ConfigurationNode config) throws IllegalNameException {
        // Name validation
        if (name == null || name.isEmpty()) {
            throw new IllegalNameException("Name is empty");
        }

        // Back up previous claims information
        List<String> claims = Collections.emptyList();
        {
            ModularConfigurationEntry<SavedAttachmentModel> entry = container.getIfExists(name);
            if (entry != null && entry.getConfig().contains("claims")) {
                claims = new ArrayList<>(entry.getConfig().getList("claims", String.class));
            }
        }

        // Store the entry
        ModularConfigurationEntry<SavedAttachmentModel> entry = container.add(name, config);
        entry.getWritableConfig().set("claims", claims);
        return entry.get();
    }

    /**
     * Gets the attachment configuration of a model name
     *
     * @param name Name of the saved model
     * @return attachment configuration, null if not found
     */
    public SavedAttachmentModel getModel(String name) {
        ModularConfigurationEntry<SavedAttachmentModel> entry = container.getIfExists(name);
        return entry == null ? null : entry.get();
    }

    /**
     * Gets the properties of a saved attachment model. If one by this name does not
     * exist, returns a default fallback. Use {@link SavedAttachmentModel#isNone()}
     * to check for this.
     *
     * @param name Name of the saved attachment model
     * @return saved model
     */
    public abstract SavedAttachmentModel getModelOrNone(String name);

    /**
     * Gets the attachment model configuration that a player is editing.
     * Returns <i>null</i> if the player isn't editing any of these models.
     * If the model does not yet exist, it is created and stored in this store,
     * and configured with a default attachment ITEM configuration.
     *
     * @param player Player
     * @return SavedAttachmentModel being edited by this player, or <i>null</i>
     *         if the player isn't editing any model configurations
     */
    public final SavedAttachmentModel getEditingInit(Player player) {
        return getEditingInit(player.getUniqueId());
    }

    /**
     * Gets the attachment model configuration that a player is editing.
     * Returns <i>null</i> if the player isn't editing any of these models.
     * If the model does not yet exist, it is created and stored in this store,
     * and configured with a default attachment ITEM configuration.
     *
     * @param playerUUID UUID of the Player
     * @return SavedAttachmentModel being edited by this player, or <i>null</i>
     *         if the player isn't editing any model configurations
     */
    public abstract SavedAttachmentModel getEditingInit(UUID playerUUID);

    /**
     * Gets the attachment model configuration that a player is editing.
     * Returns <i>null</i> if the player isn't editing any of these models.
     * The saved model might not yet exist, which should be checked with
     * {@link SavedAttachmentModel#isNone()}.
     *
     * @param player Player
     * @return SavedAttachmentModel being edited by this player, or <i>null</i>
     *         if the player isn't editing any model configurations
     */
    public final SavedAttachmentModel getEditing(Player player) {
        return getEditing(player.getUniqueId());
    }

    /**
     * Gets the attachment model configuration that a player is editing.
     * Returns <i>null</i> if the player isn't editing any of these models.
     * The saved model might not yet exist, which should be checked with
     * {@link SavedAttachmentModel#isNone()}.
     *
     * @param playerUUID UUID of the Player
     * @return SavedAttachmentModel being edited by this player, or <i>null</i>
     *         if the player isn't editing any model configurations
     */
    public abstract SavedAttachmentModel getEditing(UUID playerUUID);

    /**
     * Sets the attachment model configuration that a player is currently editing.
     * Specify a <i>null</i> model to stop editing any models.
     * Any active attachment editors are notified of this change if needed.
     *
     * @param player Player
     * @param model Model configuration to edit, <i>null</i> to edit none
     */
    public final void setEditing(Player player, SavedAttachmentModel model) {
        setEditing(player.getUniqueId(), model);
    }

    /**
     * Sets the attachment model configuration that a player is currently editing.
     * Specify a <i>null</i> model to stop editing any models.
     * Any active attachment editors are notified of this change if needed.
     *
     * @param playerUUID UUID of the Player
     * @param model Model configuration to edit, <i>null</i> to edit none
     */
    public abstract void setEditing(UUID playerUUID, SavedAttachmentModel model);

    /**
     * Gets the writable configuration for a saved attachment model
     * 
     * @param name of the saved attachment model
     * @return configuration, null if the model is not stored
     */
    public ConfigurationNode getConfig(String name) {
        ModularConfigurationEntry<SavedAttachmentModel> entry = container.getIfExists(name);
        return entry == null ? null : entry.getConfig();
    }

    /**
     * Attempts to find a String token that starts with the name of a saved attachment model.
     * First searches modules, then searches the default store.
     * 
     * @param text to find a name in
     * @return name found, null if none found
     */
    public String findName(String text) {
        String foundName = null;
        for (String name : this.getNames()) {
            if (text.startsWith(name) && (foundName == null || name.length() > foundName.length())) {
                foundName = name;
            }
        }
        return foundName;
    }

    /**
     * Tries to remove saved attachment model by name.
     * If the same name exists multiple times in different modules, only one
     * instance is removed.
     * 
     * @param name
     * @return True if found and removed
     */
    public boolean remove(String name) {
        try {
            return this.container.remove(name) != null;
        } catch (ReadOnlyModuleException ex) {
            return false; //TODO: Do something with this?
        }
    }

    /**
     * Tries to rename saved attachment model.
     * If the same name exists multiple times in different modules, only one
     * instance is renamed.
     * 
     * @param name
     * @param newName
     * @return True if found and renamed
     */
    public boolean rename(String name, String newName) {
        return container.rename(name, newName);
    }

    /**
     * Get a list of all saved attachment models
     * 
     * @return A List of the names of all saved attachment models
     */
    public List<String> getNames() {
        return this.container.getNames();
    }

    /**
     * Main (Default) store
     */
    private static class DefaultStore extends SavedAttachmentModelStore {
        private final ModularConfig modularConfig;
        private final Map<UUID, String> playerEditedModelNames = new HashMap<>();

        public DefaultStore(TrainCarts traincarts, ModularConfig modularConfig) {
            super(traincarts, modularConfig);
            this.modularConfig = modularConfig;
        }

        @Override
        public void save(boolean autosave) {
            if (autosave) {
                modularConfig.saveChanges();
            } else {
                modularConfig.save();
            }
        }

        @Override
        public void reload() {
            modularConfig.reload();
        }

        @Override
        public boolean isDefault() {
            return true;
        }

        @Override
        public List<String> getModuleNames() {
            return modularConfig.MODULES.getFileNames();
        }

        @Override
        public SavedAttachmentModelStore getModule(String moduleName) {
            ModularConfigurationFile<SavedAttachmentModel> module = modularConfig.MODULES.getFile(moduleName);
            return module == null ? null : createModule(module);
        }

        @Override
        public void setModuleNameOfModel(String name, String module) {
            ModularConfigurationEntry<SavedAttachmentModel> entry = modularConfig.getIfExists(name);
            if (entry != null) {
                entry.setModule(modularConfig.createModule(module));
            }
        }

        @Override
        public SavedAttachmentModel getModelOrNone(String name) {
            return modularConfig.get(name).get();
        }

        @Override
        public SavedAttachmentModel getEditingInit(UUID playerUUID) {
            String modelName = playerEditedModelNames.get(playerUUID);
            if (modelName == null) {
                return null;
            } else {
                try {
                    return setDefaultConfigIfMissing(modelName);
                } catch (IllegalNameException e) {
                    playerEditedModelNames.remove(playerUUID);
                    traincarts.getLogger().log(Level.SEVERE, "Unexpected illegal name exception", e);
                    return null;
                }
            }
        }

        @Override
        public SavedAttachmentModel getEditing(UUID playerUUID) {
            String modelName = playerEditedModelNames.get(playerUUID);
            return (modelName == null) ? null : getModelOrNone(modelName);
        }

        @Override
        public void setEditing(UUID playerUUID, SavedAttachmentModel model) {
            boolean changed;
            if (model == null) {
                changed = (playerEditedModelNames.remove(playerUUID) != null);
            } else {
                changed = (!model.getName().equals(playerEditedModelNames.put(playerUUID, model.getName())));
            }
            if (changed) {
                AttachmentEditor.reloadAttachmentEditorFor(playerUUID);
            }
        }
    }

    /**
     * A sub-module of saved attachment models inside the SavedModelModules folder
     */
    public static class ModuleStore extends SavedAttachmentModelStore {
        private final ModularConfigurationModule<SavedAttachmentModel> module;

        public ModuleStore(TrainCarts traincarts, ModularConfigurationModule<SavedAttachmentModel> module) {
            super(traincarts, module);
            this.module = module;
        }

        @Override
        public void save(boolean autosave) {
            if (autosave) {
                module.saveChanges();
            } else {
                module.save();
            }
        }

        @Override
        public void reload() {
            module.reload();
        }

        @Override
        public boolean isDefault() {
            return false;
        }

        @Override
        public List<String> getModuleNames() {
            return Collections.emptyList();
        }

        @Override
        public SavedAttachmentModelStore getModule(String moduleName) {
            return null;
        }

        @Override
        public void setModuleNameOfModel(String name, String module) {
        }

        @Override
        public SavedAttachmentModel getModelOrNone(String name) {
            return module.getMain().get(name).get();
        }

        @Override
        public SavedAttachmentModel getEditingInit(UUID playerUUID) {
            return traincarts.getSavedAttachmentModels().getEditingInit(playerUUID);
        }

        @Override
        public SavedAttachmentModel getEditing(UUID playerUUID) {
            return traincarts.getSavedAttachmentModels().getEditing(playerUUID);
        }

        @Override
        public void setEditing(UUID playerUUID, SavedAttachmentModel model) {
            traincarts.getSavedAttachmentModels().setEditing(playerUUID, model);
        }
    }

    private static class ModularConfig extends BasicModularConfiguration<SavedAttachmentModel> {
        private final TrainCarts traincarts;

        public ModularConfig(TrainCarts plugin, String mainFilePath, String moduleDirectoryPath) {
            super(plugin, mainFilePath, moduleDirectoryPath);
            this.traincarts = plugin;
            this.addResourcePack(TCConfig.resourcePack, "traincarts", "saved_models");
        }

        @Override
        protected void preProcessModuleConfiguration(ConfigurationNode moduleConfig) {
            storeSavedNameInConfig(moduleConfig);
        }

        @Override
        protected void postProcessEntryConfiguration(ModularConfigurationEntry<SavedAttachmentModel> entry) {
            ConfigurationNode config = entry.getWritableConfig();
            if (!config.contains(KEY_SAVED_NAME) ||
                !config.get(KEY_SAVED_NAME, "").equals(entry.getName()))
            {
                config.set(KEY_SAVED_NAME, entry.getName());
            }
        }

        @Override
        protected SavedAttachmentModel decodeConfig(ModularConfigurationEntry<SavedAttachmentModel> entry) {
            return new SavedAttachmentModel(entry);
        }

        private void storeSavedNameInConfig(ConfigurationNode savedModelsConfig) {
            // Stores the key of each saved attachment model node in the configuration itself
            // This is done automatically, so this is a migration method to update old config
            // If people change this field, revert that and log a warning
            boolean logSavedNameFieldWarning = false;
            for (ConfigurationNode config : savedModelsConfig.getNodes()) {
                if (!config.contains(KEY_SAVED_NAME)) {
                    config.set(KEY_SAVED_NAME, config.getName());
                    continue;
                }

                String setName = config.get(KEY_SAVED_NAME, config.getName());
                if (!config.getName().equals(setName)) {
                    // Note: people may have intended to rename these properties
                    // It is best to notify about this.
                    traincarts.log(Level.WARNING, "Saved attachment model '" + config.getName() + "' has a different "
                            + "name set: '" + setName + "'");
                    logSavedNameFieldWarning = true;

                    config.set(KEY_SAVED_NAME, config.getName());
                }
            }
            if (logSavedNameFieldWarning) {
                traincarts.log(Level.WARNING, "If the intention was to rename the model, instead "
                        + "rename the key, not field '" + KEY_SAVED_NAME + "'");
            }
        }
    }
}
