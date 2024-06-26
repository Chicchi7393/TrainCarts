package com.bergerkiller.bukkit.tc.attachments.ui.models.listing;

import java.util.Locale;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import org.bukkit.ChatColor;

/**
 * A single directory containing item models or other directories
 */
public final class ListedDirectory extends ListedEntry {
    private final ListedNamespace namespace;
    private final String path;
    private final String name;
    private final String nameLowerCase;

    public ListedDirectory(ListedNamespace namespace, String path) {
        this.namespace = namespace;
        this.path = path;

        int lastIdx = path.lastIndexOf('/');
        if (lastIdx == -1) {
            this.name = path;
        } else {
            this.name = path.substring(lastIdx + 1);
        }
        this.nameLowerCase = this.name.toLowerCase(Locale.ENGLISH);
    }

    private ListedDirectory(ListedNamespace namespace, ListedDirectory directory) {
        this.namespace = namespace;
        this.path = directory.path;
        this.name = directory.name;
        this.nameLowerCase = directory.nameLowerCase;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String nameLowerCase() {
        return nameLowerCase;
    }

    /**
     * Gets the path of this directory, excluding the namespace prefix
     *
     * @return path
     */
    public String path() {
        return path;
    }

    @Override
    public String fullPath() {
        return namespace.fullPath() + path;
    }

    @Override
    public int sortPriority() {
        return 2;
    }

    @Override
    public ListedNamespace namespace() {
        return namespace;
    }

    @Override
    public CommonItemStack createIconItem(DialogBuilder options) {
        return CommonItemStack.copyOf(options.getDirectoryIconItem())
                .setCustomNameMessage(ChatColor.YELLOW + this.name)
                .addLoreMessage(ChatColor.WHITE.toString() + ChatColor.ITALIC + this.fullPath())
                .addLoreLine()
                .addLoreMessage(ChatColor.DARK_GRAY + "Directory")
                .addLoreMessage(ChatColor.DARK_GRAY +
                        "< " + ChatColor.GRAY + this.nestedItemCount + ChatColor.DARK_GRAY + " Item models >");
    }

    @Override
    public String toString() {
        return "Directory: " + path;
    }

    @Override
    protected ListedDirectory cloneSelf(ListedNamespace namespace) {
        if (namespace == null) {
            throw new IllegalArgumentException("Namespace is required");
        }

        ListedDirectory clone = new ListedDirectory(namespace, this);
        clone.namespace.directories.put(clone.path, clone);
        return clone;
    }

    @Override
    protected ListedDirectory findOrCreateInRoot(ListedRoot root) {
        // Handle parent entry first - we need namespace information
        ListedEntry newParent = this.parent().findOrCreateInRoot(root);
        // Check to see if this directory already exists for the parent
        // If it does, return that one. Otherwise, create a new one
        return newParent.namespace().findOrCreateDirectory(this.path);
    }
}
