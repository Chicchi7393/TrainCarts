package com.bergerkiller.bukkit.tc.debug;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.WeakHashMap;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.HumanHand;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZone;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCache;

/**
 * Manages the different functionalities provided by /train debug [type]
 */
public class DebugTool {
    private static final WeakHashMap<Player, DebounceLogic> debounce = new WeakHashMap<>();

    /**
     * Shows a box-shaped particle display for all mutex zones for a few seconds
     *
     * @param traincarts
     * @param player
     */
    public static void showMutexZones(final TrainCarts traincarts, final Player player) {
        Location loc = player.getEyeLocation();
        final List<MutexZone> zones = MutexZoneCache.findNearbyZones(
                OfflineWorld.of(loc.getWorld()),
                new IntVector3(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()),
                32);

        if (zones.isEmpty()) {
            return;
        }

        Random r = new Random();
        for (MutexZone zone : zones) {
            if (zone.slot.isAnonymous()) {
                r.setSeed(zone.showDebugColorSeed());
            } else {
                r.setSeed(zone.slot.getName().hashCode());
            }
            java.awt.Color awt_color = java.awt.Color.getHSBColor(r.nextFloat(), 1.0f, 1.0f);
            Color color = Color.fromRGB(awt_color.getRed(), awt_color.getGreen(), awt_color.getBlue());
            zone.showDebug(player, color);
        }
    }

    public static boolean updateToolItem(Player player, CommonItemStack item) {
        return updateToolItem(player, item.toBukkit());
    }

    public static boolean updateToolItem(Player player, ItemStack item) {
        CommonItemStack inMainHand = CommonItemStack.of(HumanHand.getItemInMainHand(player));
        if (
                !inMainHand.isEmpty() &&
                inMainHand.hasCustomData() &&
                inMainHand.getCustomData().containsKey("TrainCartsDebug")
        ) {
            HumanHand.setItemInMainHand(player, item);
            return true;
        }

        return false;
    }

    /**
     * Called when a player interacts with a block using a (stick) debug item
     *
     * @param traincarts TrainCarts main plugin instance
     * @param player
     * @param clickedBlock
     * @param item
     * @param isRightClick Whether this is a right click interaction (true) or left-click break (false)
     * @return true if handled, false if not and should interact like normal
     */
    public static boolean onDebugInteract(TrainCarts traincarts, Player player, Block clickedBlock, ItemStack item, boolean isRightClick) {
        return onDebugInteract(traincarts, player, clickedBlock, CommonItemStack.of(item), isRightClick);
    }

    /**
     * Called when a player interacts with a block using a (stick) debug item
     * 
     * @param traincarts TrainCarts main plugin instance
     * @param player
     * @param clickedBlock
     * @param item
     * @param isRightClick Whether this is a right click interaction (true) or left-click break (false)
     * @return true if handled, false if not and should interact like normal
     */
    public static boolean onDebugInteract(TrainCarts traincarts, Player player, Block clickedBlock, CommonItemStack item, boolean isRightClick) {
        if (item.isEmpty() || !item.hasCustomData()) {
            return false;
        }

        CommonTagCompound tag = item.getCustomData();
        String debugType = tag.getValue("TrainCartsDebug", String.class);
        if (debugType == null) {
            return false;
        }

        if (!Permission.DEBUG_COMMAND_DEBUG.has(player)) {
            if (debounce(player)) {
                player.sendMessage(ChatColor.RED + "No permission to use this item!");
            }
            return true;
        }

        Optional<DebugToolType> match = DebugToolTypeRegistry.match(debugType);
        if (!match.isPresent()) {
            if (debounce(player)) {
                player.sendMessage(ChatColor.RED + "Item has an unknown debug mode: " + debugType);
            }
            return true;
        }

        // Load metadata
        match.get().loadMetadata(tag);

        // Left click check
        if (!isRightClick && !match.get().handlesLeftClick()) {
            return false;
        }

        // Handle logic
        if (debounce(player)) {
            match.get().onBlockInteract(traincarts, player, clickedBlock, item, isRightClick);
        }

        return true;
    }

    private static boolean debounce(Player player) {
        return debounce.computeIfAbsent(player, DebounceLogic::new).check();
    }

    private static final class DebounceLogic {
        private int lastActivation;
        private int clickStart;

        public DebounceLogic(Player player) {
            this.lastActivation = 0;
            this.clickStart = 0;
        }

        public boolean check() {
            int ticks = CommonUtil.getServerTicks();
            int timeSinceActivation = (ticks - lastActivation);
            lastActivation = ticks;

            if (timeSinceActivation > 10) {
                // If it's been a while since last activation, we do the first-time single click activation
                clickStart = ticks;
                return true;
            } else if (timeSinceActivation == 0) {
                // Same tick, suppress
                return false;
            } else {
                // Requires some delay between initial click and subsequent ones
                return (ticks - clickStart) > 10;
            }
        }
    }
}
