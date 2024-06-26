package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.localization.LocalizationEnum;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;

import java.util.HashSet;

import com.bergerkiller.bukkit.tc.controller.global.TrainCartsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.incendo.cloud.caption.Caption;

public class Localization extends LocalizationEnum {
    public static final Localization COMMAND_USAGE = new Localization("command.usage", ChatColor.GREEN + "See " + "[" + ChatColor.WHITE + ChatColor.UNDERLINE + "the WIKI](https://wiki.traincarts.net/p/TrainCarts)" + ChatColor.RESET + ChatColor.GREEN + " for more information, or use /train help");
    public static final Localization COMMAND_NOPERM = new Localization("command.noperm", ChatColor.RED + "You do not have permission, ask an admin to do this for you.");

    public static final Localization COMMAND_SAVEDTRAIN_CLAIMED = new Localization("command.savedtrain.claimed", ChatColor.RED + "Saved train with name %0% is claimed by someone else, you can not access it!");
    public static final Localization COMMAND_SAVEDTRAIN_GLOBAL_NOPERM = new Localization("command.savedtrain.global.noperm", ChatColor.RED + "You do not have permission to force access to saved trains by others, ask an admin to do this for you.");
    public static final Localization COMMAND_SAVEDTRAIN_NOTFOUND = new Localization("command.savedtrain.notfound", ChatColor.RED + "Saved train with name %0% does not exist!");
    public static final Localization COMMAND_SAVEDTRAIN_FORCE = new Localization("command.savedtrain.force", ChatColor.RED + "Saved train with name %0% is claimed by someone else, you can access it anyway with --force");
    public static final Localization COMMAND_SAVEDTRAIN_CLAIM_INVALID = new Localization("command.savedtrain.claim.invalid", ChatColor.RED + "Invalid player name specified: %0%");
    public static final Localization COMMAND_SAVEDTRAIN_INVALID_NAME = new Localization("command.savedtrain.name.invalid", ChatColor.RED + "Invalid train name: %0%");

    public static final Localization COMMAND_IMPORT_MISSING_MODELS = new Localization("command.import.models.missing", ChatColor.YELLOW + "The imported train configuration includes model configurations [" +
            ChatColor.WHITE + "%0%" + ChatColor.YELLOW + "], import them by specifying --import-models");
    public static final Localization COMMAND_IMPORT_UPDATED_MODELS = new Localization("command.import.models.updated", ChatColor.GREEN + "Imported model configurations: %0%");
    public static final Localization COMMAND_IMPORT_NO_CARTS = new Localization("command.import.nocarts", ChatColor.RED + "Imported configuration does not include any carts!");
    public static final Localization COMMAND_IMPORT_ERROR = new Localization("command.import.error", ChatColor.RED + "An error occurred trying to import the train configuration: %0%");
    public static final Localization COMMAND_IMPORT_FORBIDDEN_CONTENTS = new Localization("command.import.forbiddencontents", ChatColor.RED + "The train configuration could not be imported because it contains things you have no permission to use or spawn");

    public static final Localization COMMAND_SAVE_NEW = new Localization("command.save.new", ChatColor.GREEN + "The train was saved as %0%");
    public static final Localization COMMAND_SAVE_OVERWRITTEN = new Localization("command.save.overwritten", ChatColor.GREEN + "The train was saved as %0%, a previous train was overwritten");
    public static final Localization COMMAND_SAVE_LOCK_ORIENTATION = new Localization("command.save.lockorientation", ChatColor.YELLOW + "Train orientation is now locked to the current forward direction!\n" +
            ChatColor.YELLOW + "Future saves without --lockorientation passed will remember this orientation.\n" +
            ChatColor.YELLOW + "This can be turned off using " + ChatColor.WHITE + "/savedtrain %0% lockorientation false");
    public static final Localization COMMAND_SAVE_FORBIDDEN_CONTENTS = new Localization("command.save.forbiddencontents", ChatColor.RED + "The train configuration could not be saved because the train contains things you have no permission to use or spawn");

    public static final Localization COMMAND_MODEL_CONFIG_CLAIMED = new Localization("command.model.config.claimed", ChatColor.RED + "Saved model configuration with name %0% is claimed by someone else, you can not access it!");
    public static final Localization COMMAND_MODEL_CONFIG_GLOBAL_NOPERM = new Localization("command.model.config.global.noperm", ChatColor.RED + "You do not have permission to force access to saved model configurations by others, ask an admin to do this for you.");
    public static final Localization COMMAND_MODEL_CONFIG_NOTFOUND = new Localization("command.model.config.notfound", ChatColor.RED + "Saved model configuration with name %0% does not exist!");
    public static final Localization COMMAND_MODEL_CONFIG_FORCE = new Localization("command.model.config.force", ChatColor.RED + "Saved model configuration with name %0% is claimed by someone else, you can access it anyway with --force");
    public static final Localization COMMAND_MODEL_CONFIG_INVALID_NAME = new Localization("command.model.config.name.invalid", ChatColor.RED + "Invalid model configuration name: %0%");
    public static final Localization COMMAND_MODEL_CONFIG_INPUT_NAME_EMPTY = new Localization("command.model.config.name.empty", ChatColor.RED + "Input model name is empty!");
    public static final Localization COMMAND_MODEL_CONFIG_INPUT_NAME_INVALID = new Localization("command.model.config.name.invalid", ChatColor.RED + "Input model name '%0%' contains invalid characters!");
    public static final Localization COMMAND_MODEL_CONFIG_EDIT_EXISTING = new Localization("command.model.config.edit.existing", ChatColor.GREEN + "You are now editing the model configuration '" + ChatColor.YELLOW + "%0%" + ChatColor.GREEN + "'!");
    public static final Localization COMMAND_MODEL_CONFIG_EDIT_NEW = new Localization("command.model.config.edit.new", ChatColor.GREEN + "You are now editing the " + ChatColor.BLUE + "NEW" + ChatColor.GREEN + " model configuration '" + ChatColor.YELLOW + "%0%" + ChatColor.GREEN + "'!");

    public static final Localization COMMAND_TICKET_NOTFOUND = new Localization("command.ticket.notfound", ChatColor.RED + "Ticket with name %0% does not exist");
    public static final Localization COMMAND_TICKET_NOTEDITING = new Localization("command.ticket.notediting",
            ChatColor.RED + "You are not editing any tickets right now\n" +
            ChatColor.RED + "To create a new train ticket, use /train ticket create\n" +
            ChatColor.RED + "To edit an existing train ticket, use /train ticket edit [name]");

    public static final Localization COMMAND_EFFECT_PLAY = new Localization("command.effect.play", ChatColor.GREEN + "Playing effect " + ChatColor.YELLOW + "%0%");
    public static final Localization COMMAND_EFFECT_STOP = new Localization("command.effect.stop", ChatColor.YELLOW + "Stopping effect " + ChatColor.WHITE + "%0%");
    public static final Localization COMMAND_EFFECT_REPLAY = new Localization("command.effect.replay", ChatColor.GREEN + "Re-playing effect " + ChatColor.YELLOW + "%0%");

    public static final Localization COMMAND_TRAIN_NOT_FOUND = new Localization("command.input.train.notfound", ChatColor.RED + "Train with name %0% does not exist");
    public static final Localization COMMAND_CART_NOT_FOUND_IN_TRAIN = new Localization("command.input.cart.notintrain", ChatColor.RED + "Cart '%0%' does not exist in the selected train");
    public static final Localization COMMAND_CART_NOT_FOUND_BY_UUID = new Localization("command.input.cart.uuidnotfound", ChatColor.RED + "Cart with unique ID %0% does not exist");
    public static final Localization COMMAND_CART_NOT_FOUND_NEARBY = new Localization("command.input.cart.notnearby", ChatColor.RED + "No cart was found near the specified coordinates");
    public static final Localization COMMAND_INPUT_SPEED_INVALID = new Localization("command.input.speed.invalid", ChatColor.RED + "Input value %0% is not a valid number or speed expression");
    public static final Localization COMMAND_INPUT_ACCELERATION_INVALID = new Localization("command.input.acceleration.invalid", ChatColor.RED + "Input value %0% is not a valid number or acceleration expression");
    public static final Localization COMMAND_INPUT_DIRECTION_INVALID = new Localization("command.input.direction.invalid", ChatColor.RED + "Input value %0% is not a valid direction");
    public static final Localization COMMAND_INPUT_CHUNK_LOADING_MODE_INVALID = new Localization("command.input.chunkloading.mode.invalid", ChatColor.RED + "Input value %0% is not a valid chunk loading mode");
    public static final Localization COMMAND_INPUT_NAME_EMPTY = new Localization("command.input.name.empty", ChatColor.RED + "Input train name is empty!");
    public static final Localization COMMAND_INPUT_NAME_INVALID = new Localization("command.input.name.invalid", ChatColor.RED + "Input train name '%0%' contains invalid characters!");
    public static final Localization COMMAND_INPUT_ATTACHMENTS_NO_SEATS = new Localization("command.input.attachments.noseats", ChatColor.RED + "No seats with name '%0%' found!");
    public static final Localization COMMAND_INPUT_ATTACHMENTS_NO_EFFECTS = new Localization("command.input.attachments.noeffects", ChatColor.RED + "No effects with name '%0%' found!");
    public static final Localization COMMAND_INPUT_SELECTOR_INVALID = new Localization("command.input.selector.invalid", ChatColor.RED + "[TrainCarts] Selector condition contains syntax errors!");
    public static final Localization COMMAND_INPUT_SELECTOR_NOPERM = new Localization("command.input.selector.noperm", ChatColor.RED + "[TrainCarts] You do not have permission to use TrainCarts command selectors!");
    public static final Localization COMMAND_INPUT_SELECTOR_EXCEEDEDLIMIT = new Localization("command.input.selector.exceededlimit", ChatColor.RED + "[TrainCarts] Selector expression matched too many results!");

    public static final Localization PROPERTY_NOTFOUND = new Localization("property.notfound", ChatColor.RED + "Property with name '%0%' does not exist");
    public static final Localization PROPERTY_ERROR = new Localization("property.error", ChatColor.RED + "An internal error occurred while parsing value '%1%' for property '%0%'");
    public static final Localization PROPERTY_INVALID_INPUT = new Localization("property.invalidinput", ChatColor.RED + "Value '%1%' for property '%0%' is invalid: %2%");
    public static final Localization PROPERTY_NOPERM_ANY = new Localization("property.nopermissionany", ChatColor.RED + "You do not have permission to modify train properties");
    public static final Localization PROPERTY_NOPERM = new Localization("property.nopermission", ChatColor.RED + "You do not have permission to modify the property with name '%0%'");

    public static final Localization EDIT_SUCCESS = new Localization("edit.success", ChatColor.GREEN + "You are now editing train '" + ChatColor.YELLOW + "%0%" + ChatColor.GREEN + "'!");
    public static final Localization EDIT_NOSELECT = new Localization("edit.noselect", ChatColor.YELLOW + "You haven't selected a train to edit yet!");
    public static final Localization EDIT_NOTALLOWED = new Localization("edit.notallowed", ChatColor.RED + "You are not allowed to own trains!");
    public static final Localization EDIT_NONEFOUND = new Localization("edit.nonefound", ChatColor.RED + "You do not own any trains you can edit.");
    public static final Localization EDIT_NOTFOUND = new Localization("edit.notfound", ChatColor.RED + "Could not find a valid train named '%0%'!");
    public static final Localization EDIT_NOTOWNED = new Localization("edit.notowned", ChatColor.RED + "You do not own this train!");
    public static final Localization EDIT_NOTLOADED = new Localization("edit.notloaded", ChatColor.RED + "The selected train is not loaded right now!");

    public static final Localization SPAWN_DISALLOWED_TYPE = new Localization("spawn.type.notallowed", ChatColor.RED + "You do not have permission to create minecarts of type %0%");
    public static final Localization SPAWN_DISALLOWED_INVENTORY = new Localization("spawn.inventoryitems.notallowed", ChatColor.RED + "You do not have permission to create minecarts with pre-existing inventory items");
    public static final Localization SPAWN_FORBIDDEN_CONTENTS = new Localization("spawn.forbiddencontents", ChatColor.RED + "The train configuration cannot be spawned because the train contains things you have no permission to use or spawn");
    public static final Localization SPAWN_MAX_PER_WORLD = new Localization("spawn.maxperworld", ChatColor.RED + "Cannot spawn because the maximum number of Minecarts on this world has been reached!");
    public static final Localization SPAWN_TOO_LONG = new Localization("spawn.toolong", ChatColor.RED + "Cannot spawn because the spawned train is too long! (Too many Minecarts)");

    public static final Localization SELECT_DESTINATION = new Localization("select.destination", ChatColor.YELLOW + "You have selected " + ChatColor.WHITE + "%0%" + ChatColor.YELLOW + " as your destination!");
    public static final Localization TICKET_EXPIRED = new Localization("ticket.expired", ChatColor.RED + "Your ticket for %0% is expired");
    public static final Localization TICKET_REQUIRED = new Localization("ticket.required", ChatColor.RED + "You do not own a ticket for this train!");
    public static final Localization TICKET_USED = new Localization("ticket.used", ChatColor.GREEN + "You have used your " + ChatColor.YELLOW + "%0%" + ChatColor.GREEN + " ticket!");
    public static final Localization TICKET_CONFLICT = new Localization("ticket.conflict", ChatColor.RED + "You own multiple tickets that can be used for this train. Please hold the right ticket in your hand!");
    public static final Localization TICKET_CONFLICT_OWNER = new Localization("ticket.ownerConflict", ChatColor.RED + "The train ticket %0% is not yours, it belongs to %1%!");
    public static final Localization TICKET_CONFLICT_TYPE = new Localization("ticket.typeConflict", ChatColor.RED + "The train ticket %0% can not be used for this train!");
    public static final Localization WAITER_TARGET_NOT_FOUND = new Localization("waiter.notfound", ChatColor.RED + "Didn't find a " + ChatColor.YELLOW + "%0%" + ChatColor.RED + " sign on the track!");

    public static final Localization TICKET_ADD = new Localization("ticket.add", ChatColor.WHITE + "[Ticket System]" + ChatColor.YELLOW + " You received %0% in your bank account!");
    public static final Localization TICKET_CHECK = new Localization("ticket.check", ChatColor.WHITE + "[Ticket System]" + ChatColor.YELLOW + " You currently have %0% in your bank account!");
    public static final Localization TICKET_BUYFAIL = new Localization("ticket.buyfail", ChatColor.WHITE + "[Ticket System]" + ChatColor.RED + " You can't afford a Ticket for %0%, sorry.");
    public static final Localization TICKET_BUY = new Localization("ticket.buy", ChatColor.WHITE + "[Ticket System]" + ChatColor.YELLOW + " You bought a Ticket for %0%.");
    public static final Localization TICKET_BUYOWNER = new Localization("ticket.buyowner", ChatColor.WHITE + "[Ticket System]" + ChatColor.YELLOW + " %0% " + ChatColor.YELLOW + "bought a Ticket for %1% on " + ChatColor.WHITE + "%2%" + ChatColor.YELLOW + ".");
    public static final Localization TICKET_MAP_INVALID = new Localization("ticket.map.invalid", "Invalid Ticket");
    public static final Localization TICKET_MAP_EXPIRED = new Localization("ticket.map.expired", "EXPIRED");
    public static final Localization TICKET_MAP_USES = new Localization("ticket.map.uses", "%1%/%0% uses") {
        @Override
        public void writeDefaults(ConfigurationNode config, String path) {
            ConfigurationNode node = config.getNode(path);
            node.set("1", "Single use");
            node.set("-1", "Unlimited uses");
            node.set("default", "%1%/%0% uses");
        }
    };

    // pathfinding
    public static final Localization PATHING_BUSY = new Localization("pathfinding.busy", ChatColor.YELLOW + "Looking for a way to reach the destination...");
    public static final Localization PATHING_FAILED = new Localization("pathfinding.failed", ChatColor.RED + "Destination " + ChatColor.YELLOW + "%0%" + ChatColor.RED + " could not be reached from here!");

    // train storing chest
    public static final Localization CHEST_NOPERM = new Localization("chest.noperm", ChatColor.RED + "You do not have permission to use the train storage chest!");
    public static final Localization CHEST_NOITEM = new Localization("chest.noitem", ChatColor.RED + "You are not currently holding a train storage chest item!");
    public static final Localization CHEST_GIVE = new Localization("chest.give", ChatColor.GREEN + "You have been given a train storage chest item. Use it to store and spawn trains");
    public static final Localization CHEST_GIVE_TO = new Localization("chest.giveto", ChatColor.GREEN + "Gave a train storage chest item to player %0%");
    public static final Localization CHEST_UPDATE = new Localization("chest.update", ChatColor.GREEN + "Your train storage chest item has been updated");
    public static final Localization CHEST_LOCKED = new Localization("chest.locked", ChatColor.RED + "Your train storage chest item is locked and can not pick up the train");
    public static final Localization CHEST_PICKUP = new Localization("chest.pickup", ChatColor.GREEN + "Train picked up and stored inside the item!");
    public static final Localization CHEST_FULL = new Localization("chest.full", ChatColor.RED + "Your train storage chest item is full and can not pick up the train");
    public static final Localization CHEST_IMPORTED = new Localization("chest.imported", ChatColor.GREEN + "The train was imported into the chest item");
    public static final Localization CHEST_SPAWN_SUCCESS = new Localization("chest.spawn.success", ChatColor.GREEN + "Train stored inside the item has been spawned on the rails");
    public static final Localization CHEST_SPAWN_EMPTY = new Localization("chest.spawn.empty", ChatColor.RED + "Train can not be spawned, no train is stored in the item");
    public static final Localization CHEST_SPAWN_NORAIL = new Localization("chest.spawn.norail", ChatColor.RED + "Train can not be spawned, clicked block is not a known rail");
    public static final Localization CHEST_SPAWN_NORAIL_LOOK = new Localization("chest.spawn.noraillook", ChatColor.RED + "Train can not be spawned, not looking at any rail or too far away");
    public static final Localization CHEST_SPAWN_RAILTOOSHORT = new Localization("chest.spawn.railtooshort", ChatColor.RED + "Train can not be spawned, rails not long enough to fit the train");
    public static final Localization CHEST_SPAWN_BLOCKED = new Localization("chest.spawn.blocked", ChatColor.RED + "Train can not be spawned, no space on rails because another train is in the way");
    public static final Localization CHEST_SPAWN_LIMIT_REACHED = new Localization("chest.spawn.limitreached", ChatColor.RED + "Train can not be spawned, the maximum number of spawns was reached");

    // signs
    public static final Localization SIGN_NO_PERMISSION = new Localization("sign.noperm", ChatColor.RED + "You do not have permission to use this sign! [%0%]");
    public static final Localization SIGN_NO_RC_PERMISSION = new Localization("sign.noremotecontrolperm", ChatColor.RED + "You do not have permission to use the remote control sign feature!");

    // animate command
    public static final Localization COMMAND_ANIMATE_SUCCESS = new Localization("command.animate.success", ChatColor.GREEN + "Now playing animation " + ChatColor.YELLOW + "%0%" +
            ChatColor.GREEN + " at speed " + ChatColor.YELLOW + "%1%" + ChatColor.GREEN + " with phase delay " + ChatColor.YELLOW + "%2%");
    public static final Localization COMMAND_ANIMATE_FAILURE = new Localization("command.animate.failure", ChatColor.RED + "Failed to find animation " + ChatColor.YELLOW + "%0%" + ChatColor.RED + "!");

    // Attachment editor chat/text messages
    public static final Localization ATTACHMENTS_LOAD_CLIPBOARD = new Localization("attachments.load.clipboard", ChatColor.GREEN + "Attachment loaded from your clipboard!");
    public static final Localization ATTACHMENTS_LOAD_MODEL_STORE = new Localization("attachments.load.modelstore", ChatColor.GREEN + "Attachment '%0%' loaded from the model store!");
    public static final Localization ATTACHMENTS_LOAD_PASTE_SERVER = new Localization("attachments.load.pasteserver", ChatColor.GREEN + "Attachment imported from paste server!");
    public static final Localization ATTACHMENTS_SAVE_CLIPBOARD = new Localization("attachments.save.clipboard", ChatColor.GREEN + "Attachment saved to your clipboard!");
    public static final Localization ATTACHMENTS_SAVE_MODEL_STORE = new Localization("attachments.save.modelstore", ChatColor.GREEN + "Attachment saved to the model store as %0%!");
    public static final Localization ATTACHMENTS_SAVE_PASTE_SERVER = new Localization("attachments.save.pasteserver", ChatColor.GREEN + "Attachment exported to paste server: " + ChatColor.WHITE + ChatColor.UNDERLINE + "%0%");

    private Localization(String name, String defValue) {
        super(name, defValue);
    }

    @Override
    public String get(String... arguments) {
        return TrainCarts.plugin.getLocale(this.getName(), arguments);
    }

    /**
     * Gets the cloud framework caption matching this localization
     * 
     * @return caption
     */
    public Caption getCaption() {
        return Caption.of(getName());
    }

    public void broadcast(MinecartGroup group, String... arguments) {
        HashSet<Player> receivers = new HashSet<>();
        for (MinecartMember<?> member : group) {
            // Editing
            receivers.addAll(member.getProperties().getEditingPlayers());
            // Occupants
            if (member.getEntity().hasPlayerPassenger()) {
                receivers.add(member.getEntity().getPlayerPassenger());
            }
        }
        for (Player player : receivers) {
            this.message(player, arguments);
        }
    }

    public void message(TrainCartsPlayer player, String... arguments) {
        Player onlinePlayer = player.getOnlinePlayer();
        if (onlinePlayer != null) {
            message(onlinePlayer, arguments);
        }
    }

    /**
     * Gets a boolean 'yes' or 'no' colored red/green respectively
     * 
     * @param value
     * @return Yes if true, No if false
     */
    public static String boolStr(boolean value) {
        return value ? (ChatColor.GREEN + "Yes") : (ChatColor.RED + "No");
    }
}
