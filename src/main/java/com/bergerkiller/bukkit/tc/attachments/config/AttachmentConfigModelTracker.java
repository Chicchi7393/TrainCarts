package com.bergerkiller.bukkit.tc.attachments.config;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Extends the capabilities of the {@link AttachmentConfigTracker} so that it can
 * track the MODEL attachments as well. This will cause all MODEL attachments to
 * appear as if they have an additional attachment with the model configuration.
 * Requires an attachment model store to retrieve and track these model
 * configurations. This functionality should be done by the
 * {@link #findModelConfig(String)} method, to be implemented<br>
 * <br>
 * If infinite recursion is detected, the model is only added as a child once.
 * Will automatically start tracking the underlying tracker, and all model
 * attachments, once a listener is first added. When all listeners are
 * removed, stops tracking everything automatically. Functions the same way
 * as {@link AttachmentConfigTracker} does in that regard.
 */
public abstract class AttachmentConfigModelTracker extends AttachmentConfigTrackerBase {
    private DeepAttachmentConfig root;
    private final DeepAttachmentTrackerProxy proxy;

    public AttachmentConfigModelTracker(AttachmentConfigTracker tracker) {
        this(tracker, null);
    }

    public AttachmentConfigModelTracker(AttachmentConfigTracker tracker, Plugin plugin) {
        super((plugin == null) ? Logger.getGlobal() : plugin.getLogger());
        this.root = null;
        this.proxy = new DeepAttachmentTrackerProxy(tracker) {
            @Override
            public DeepAttachmentConfig getRoot() {
                return root;
            }

            @Override
            public void setRoot(AttachmentConfig baseRoot) {
                root = createAttachmentConfig(PositionAccess.DEFAULT, null, baseRoot);
            }
        };
    }

    /**
     * Retrieves the {@link AttachmentConfigTracker} which can be used to
     * read and track the configuration of a single MODEL attachment. This
     * tracker will listen for changes in this configuration to update the
     * attachments automatically.
     *
     * @param name Model name
     * @return tracker for the configuration of the model by this name
     */
    public abstract AttachmentConfigTracker findModelConfig(String name);

    @Override
    protected void startTracking() {
        proxy.start();
    }

    @Override
    protected void stopTracking() {
        root.onRemoved();
        proxy.stop();
        root = null;
    }

    @Override
    protected AttachmentConfig getRoot() {
        return root;
    }

    /**
     * Unused for models. Sync the {@link AttachmentConfigTracker} instead, which will
     * automatically synchronize the model trackers that listen for them.
     */
    @Override
    public void sync() {
        // Unused
    }

    private DeepAttachmentConfig createAttachmentConfig(PositionAccess position, DeepAttachmentConfig parent, AttachmentConfig base) {
        if (base instanceof AttachmentConfig.Model) {
            return new DeepModelAttachmentConfig(position, parent, (AttachmentConfig.Model) base);
        } else {
            return new DeepAttachmentConfig(position, parent, base);
        }
    }

    /**
     * Re-parents an attachment configuration in a way that changes the
     * absolute paths and parent information. Wraps the original attachment
     * configuration. Only used for all attachments deep inside the
     * model attachment, recursively.
     */
    private class DeepAttachmentConfig implements AttachmentConfig {
        protected PositionAccess position;
        private final DeepAttachmentConfig parent;
        private final AttachmentConfig base;
        protected final ArrayList<DeepAttachmentConfig> children;

        public DeepAttachmentConfig(PositionAccess position, DeepAttachmentConfig parent, AttachmentConfig base) {
            this.position = position;
            this.parent = parent;
            this.base = base;

            List<AttachmentConfig> baseChildren = base.children();
            this.children = new ArrayList<>(baseChildren.size() + 1);
            for (AttachmentConfig baseChild : baseChildren) {
                this.children.add(createAttachmentConfig(position.forChildren(), this, baseChild));
            }
        }

        @Override
        public AttachmentConfig parent() {
            return parent;
        }

        @Override
        public List<AttachmentConfig> children() {
            return Collections.unmodifiableList(children);
        }

        @Override
        public int childIndex() {
            return position.childIndex(base);
        }

        @Override
        public YamlPath path() {
            return position.path(base);
        }

        @Override
        public String typeId() {
            return base.typeId();
        }

        @Override
        public ConfigurationNode config() {
            return base.config();
        }

        /**
         * Checks whether a particular model name is in use by this attachment
         *
         * @param name Model name
         * @return True if this model name is used
         */
        public final boolean isModelUsed(String name) {
            for (DeepAttachmentConfig att = this; att != null; att = att.parent) {
                if (att.isModelUsedSelf(name)) {
                    return true;
                }
            }
            return false;
        }

        protected boolean isModelUsedSelf(String name) {
            return false;
        }

        /**
         * Adds a child attachment at a certain relative path
         *
         * @param path Relative child index path
         * @param base Base attachment to add. Is wrapped into a DeepAttachmentConfig
         */
        public void addChildAtPath(int[] path, final AttachmentConfig base) {
            runActionForChild(path, (parent, childIndex) -> parent.addChild(childIndex, base));
        }

        protected void addChild(int childIndex, AttachmentConfig baseConfig) {
            if (childIndex < 0 || childIndex > children.size()) {
                throw new IndexOutOfBoundsException("Child index out of bounds: " + childIndex);
            }

            DeepAttachmentConfig deepConfig = createAttachmentConfig(position.forChildren(), this, baseConfig);
            children.add(childIndex, deepConfig);
            notifyChange(ChangeType.ADDED, deepConfig);
        }

        /**
         * Navigates to the child at the path specified and removes it
         *
         * @param path
         */
        public void removeChildAtPath(int[] path) {
            runActionForChild(path, DeepAttachmentConfig::removeChild);
        }

        protected void removeChild(int childIndex) {
            DeepAttachmentConfig deepConfig = children.get(childIndex);
            deepConfig.onRemoved();
            children.remove(childIndex);
            notifyChange(ChangeType.REMOVED, deepConfig);
        }

        public void childChangedAtPath(int[] path) {
            runActionForChild(path, DeepAttachmentConfig::childChanged);
        }

        protected void childChanged(int childIndex) {
            notifyChange(ChangeType.CHANGED, children.get(childIndex));
        }

        private void runActionForChild(int[] path, ChildActionConsumer action) {
            DeepAttachmentConfig parent = this;
            int limit = path.length - 1;
            int i = 0;
            while (i < limit) {
                parent = parent.children.get(path[i]);
                i++;
            }
            action.accept(parent, path[i]);
        }

        protected void onRemoved() {
            position = position.removed(base);
            for (DeepAttachmentConfig child : children) {
                child.onRemoved();
            }
        }
    }

    @FunctionalInterface
    private interface ChildActionConsumer {
        void accept(DeepAttachmentConfig parent, int childIndex);
    }

    /**
     * Special configuration wrapper for MODEL attachments. Tracks the changes in the
     * model configuration, forwarding those changes to the recursive children.
     * Also checks no infinite recursions happen, not adding another child model
     * if the same model name is already used for a parent.
     */
    private class DeepModelAttachmentConfig extends DeepAttachmentConfig implements AttachmentConfig.Model {
        private final AttachmentConfig.Model baseModel;
        private final DeepAttachmentTrackerProxy proxy;
        private DeepAttachmentConfig modelChild;

        public DeepModelAttachmentConfig(PositionAccess position, DeepAttachmentConfig parent, AttachmentConfig.Model base) {
            super(position, parent, base);
            this.baseModel = base;

            // Check this name isn't already used. If it is, abort adding the base model
            if (parent != null && parent.isModelUsed(base.modelName())) {
                this.modelChild = null;
                this.proxy = null;
                return;
            }

            // Find the configuration tracker to use for this model name
            AttachmentConfigTracker modelTracker = findModelConfig(base.modelName());

            // This proxy will automatically update the hidden model attachment child
            this.proxy = new DeepAttachmentTrackerProxy(modelTracker) {
                @Override
                public DeepAttachmentConfig getRoot() {
                    return modelChild;
                }

                @Override
                public void setRoot(AttachmentConfig baseRoot) {
                    if (baseRoot == null) {
                        if (!children.isEmpty() && children.get(children.size() - 1) == modelChild) {
                            children.remove(children.size() - 1);
                        }
                        modelChild = null;
                    } else {
                        modelChild = createAttachmentConfig(new PositionAccessModelChild(DeepModelAttachmentConfig.this),
                                DeepModelAttachmentConfig.this, baseRoot);
                        children.add(modelChild);
                    }
                }
            };

            // Initialize the model child and start tracking changes that happen
            proxy.start();
        }

        @Override
        protected boolean isModelUsedSelf(String name) {
            return baseModel.modelName().equals(name);
        }

        @Override
        protected void onRemoved() {
            super.onRemoved();
            if (proxy != null) {
                proxy.stop();
                modelChild = null; // But keep around in children
            }
        }

        /**
         * Gets the index of the configuration-invisible model attachment added as child of
         * this model attachment. Can't be called anymore after being removed.
         *
         * @return model child index
         */
        public int getModelChildIndex() {
            int index = children.size() - 1;
            if (modelChild == null || index == -1) {
                throw new IllegalStateException("Model configuration does not store a model attachment");
            }
            return index;
        }

        /**
         * Gets the absolute YAML path of the configuration-invisible model attachment
         * added as child of this model attachment. Can't be called anymore after being removed.
         *
         * @return model child path
         */
        public YamlPath getModelPath() {
            // Is the configuration using a list to describe attachments, or an object model?
            // Create the most appropriate path for either.
            int index = getModelChildIndex();
            if (index == 0) {
                // Assume object index
                return path().child("attachments").child("0");
            } else {
                YamlPath siblingPath = children.get(0).path();
                if (siblingPath.isListElement()) {
                    return siblingPath.parent().listChild(index);
                } else {
                    return siblingPath.parent().child(Integer.toString(index));
                }
            }
        }

        @Override
        protected void addChild(int childIndex, AttachmentConfig baseConfig) {
            // Index is not allowed to be past the model config
            if (modelChild != null && childIndex == children.size()) {
                throw new IndexOutOfBoundsException("Child index out of bounds: " + childIndex);
            } else {
                super.addChild(childIndex, baseConfig);
            }
        }

        @Override
        protected void removeChild(int childIndex) {
            // Index is not allowed to be past the model config
            if (modelChild != null && childIndex == (children.size()-1)) {
                throw new IndexOutOfBoundsException("Child index out of bounds: " + childIndex);
            } else {
                super.removeChild(childIndex);
            }
        }

        @Override
        protected void childChanged(int childIndex) {
            // Index is not allowed to be past the model config
            if (modelChild != null && childIndex == (children.size()-1)) {
                throw new IndexOutOfBoundsException("Child index out of bounds: " + childIndex);
            } else {
                super.childChanged(childIndex);
            }
        }

        @Override
        public String modelName() {
            return baseModel.modelName();
        }


    }

    /**
     * Listens for changes that occur to attachments and forwards those changes
     * to a separately managed deep attachment configuration tree.
     */
    private abstract class DeepAttachmentTrackerProxy implements AttachmentConfigListener {
        private final AttachmentConfigTracker tracker;

        /**
         * Gets the current root attachment relative to which changes are proxied
         *
         * @return root attachment
         */
        public abstract DeepAttachmentConfig getRoot();

        /**
         * Changes the root attachment. Specifies the base attachment configuration,
         * implementer should convert it into a DeepAttachmentConfig and assign it.
         *
         * @param baseRoot
         */
        public abstract void setRoot(AttachmentConfig baseRoot);

        public DeepAttachmentTrackerProxy(AttachmentConfigTracker tracker) {
            this.tracker = tracker;
        }

        /**
         * Starts tracking changes, initializing the root attachment.
         */
        public final void start() {
            setRoot(tracker.startTracking(this));
        }

        /**
         * Stops tracking, de-initializes the root attachment
         */
        public final void stop() {
            tracker.stopTracking(this);
        }

        @Override
        public void onAttachmentAdded(AttachmentConfig attachment) {
            int[] path = attachment.childPath();
            if (path.length == 0) {
                // Sanity
                if (getRoot() != null) {
                    throw new IllegalStateException("Root being re-added while one already exists");
                }

                // Add as a child of this attachment at the very end
                setRoot(attachment);
                notifyChange(AttachmentConfig.ChangeType.ADDED, getRoot());
            } else {
                // Sanity
                DeepAttachmentConfig root = getRoot();
                if (root == null) {
                    throw new IllegalStateException("Root child being added while root was removed");
                }

                // Add the attachment
                root.addChildAtPath(path, attachment);
            }
        }

        @Override
        public void onAttachmentRemoved(AttachmentConfig attachment) {
            int[] path = attachment.childPath();
            if (path.length == 0) {
                // Sanity
                DeepAttachmentConfig root = getRoot();
                if (root == null) {
                    throw new IllegalStateException("Root being removed, but root was already removed");
                }

                // Remove the current model child, then fire an event about it
                root.onRemoved();
                setRoot(null);
                notifyChange(AttachmentConfig.ChangeType.REMOVED, root);
            } else {
                // Sanity
                DeepAttachmentConfig root = getRoot();
                if (root == null) {
                    throw new IllegalStateException("Root child being removed, but root was already removed");
                }

                // Remove the attachment
                root.removeChildAtPath(path);
            }
        }

        @Override
        public void onAttachmentChanged(AttachmentConfig attachment) {
            DeepAttachmentConfig root = getRoot();
            if (root == null) {
                throw new IllegalStateException("Root changed, but root was already removed");
            }

            int[] path = attachment.childPath();
            if (path.length == 0) {
                notifyChange(AttachmentConfig.ChangeType.CHANGED, root);
            } else {
                root.childChangedAtPath(path);
            }
        }
    }

    /**
     * Retrieves child index or YAML path information for an attachment node deep
     * in the tree. This performs the right logic to re-map this information
     * for recursive model attachments.
     */
    private interface PositionAccess {
        /**
         * Default position access used from the ROOT onwards
         */
        PositionAccess DEFAULT = new PositionAccess() {
            @Override
            public int childIndex(AttachmentConfig base) {
                return base.childIndex();
            }

            @Override
            public YamlPath path(AttachmentConfig base) {
                return base.path();
            }

            @Override
            public PositionAccess forChildren() {
                return this;
            }
        };

        /**
         * Gets the child index of the attachment. Can be overridden to return
         * a different (generated) value.
         *
         * @param base Base Attachment position is translated for
         * @return child index of the attachment
         */
        int childIndex(AttachmentConfig base);

        /**
         * Produces the absolute yaml path for an attachment.
         *
         * @param base Base Attachment position is translated for
         * @return absolute yaml path
         */
        YamlPath path(AttachmentConfig base);

        /**
         * Gets the PositionAccess which should be used for the children
         * of this attachment. Might be the same instance.
         *
         * @return position access to use for child attachments
         */
        PositionAccess forChildren();

        /**
         * Makes the position access fixed, so it is no longer dynamically
         * generated. This is for removed attachments to use.
         *
         * @param base Base Attachment position is translated for
         * @return fixed
         */
        default PositionAccess removed(AttachmentConfig base) {
            return new PositionAccessRemoved(childIndex(base), path(base));
        }
    }

    private static class PositionAccessRemoved implements PositionAccess {
        private final int childIndex;
        private final YamlPath path;

        public PositionAccessRemoved(int childIndex, YamlPath path) {
            this.childIndex = childIndex;
            this.path = path;
        }

        @Override
        public int childIndex(AttachmentConfig base) {
            return childIndex;
        }

        @Override
        public YamlPath path(AttachmentConfig base) {
            return path;
        }

        @Override
        public PositionAccess forChildren() {
            throw new UnsupportedOperationException("Can't add children to removed attachments");
        }

        @Override
        public PositionAccess removed(AttachmentConfig base) {
            return this;
        }
    }

    /**
     * The position access for an invisible child added to a MODEL attachment. Can call
     * back to the parent model attachment to figure out the child index / path information.
     */
    private static class PositionAccessModelChild implements PositionAccess {
        private final DeepModelAttachmentConfig parent;
        private final PositionAccess forChildren;

        public PositionAccessModelChild(DeepModelAttachmentConfig parent) {
            this.parent = parent;
            this.forChildren = new PositionAccess() {
                @Override
                public int childIndex(AttachmentConfig base) {
                    return base.childIndex();
                }

                @Override
                public YamlPath path(AttachmentConfig base) {
                    YamlPath parentPath = PositionAccessModelChild.this.parent.getModelPath();
                    return YamlLogic.INSTANCE.join(parentPath, base.path());
                }

                @Override
                public PositionAccess forChildren() {
                    return this; // Can be used for all recursive children as well
                }
            };
        }

        @Override
        public int childIndex(AttachmentConfig base) {
            return parent.getModelChildIndex();
        }

        @Override
        public YamlPath path(AttachmentConfig base) {
            return parent.getModelPath();
        }

        @Override
        public PositionAccess forChildren() {
            return forChildren;
        }
    }
}
