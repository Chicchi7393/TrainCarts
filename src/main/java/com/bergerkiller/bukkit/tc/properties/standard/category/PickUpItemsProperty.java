package com.bergerkiller.bukkit.tc.properties.standard.category;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.commands.annotations.CommandTargetTrain;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.PropertyCheckPermission;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyParseContext;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedStandardCartProperty;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;

/**
 * Enables a storage cart to pick up items nearby off the ground
 */
public final class PickUpItemsProperty extends FieldBackedStandardCartProperty<Boolean> {

    @CommandTargetTrain
    @PropertyCheckPermission("pickupitems")
    @Command("train pickupitems|pickup <pickup>")
    @CommandDescription("Sets whether the train picks up items off the ground")
    private void setProperty(
            final CommandSender sender,
            final TrainProperties properties,
            final @Argument("pickup") boolean pickup
    ) {
        properties.set(this, pickup);
        getProperty(sender, properties);
    }

    @Command("train pickupitems|pickup")
    @CommandDescription("Displays whether the train picks up items off the ground")
    private void getProperty(
            final CommandSender sender,
            final TrainProperties properties
    ) {
        sender.sendMessage(ChatColor.YELLOW + "Train picks up items off the ground: "
                + Localization.boolStr(properties.get(this)));
    }

    @CommandTargetTrain
    @PropertyCheckPermission("pickupitems")
    @Command("cart pickupitems|pickup <pickup>")
    @CommandDescription("Sets whether the cart picks up items off the ground")
    private void setProperty(
            final CommandSender sender,
            final CartProperties properties,
            final @Argument("pickup") boolean pickup
    ) {
        properties.set(this, pickup);
        getProperty(sender, properties);
    }

    @Command("cart pickupitems|pickup")
    @CommandDescription("Displays whether the cart picks up items off the ground")
    private void getProperty(
            final CommandSender sender,
            final CartProperties properties
    ) {
        sender.sendMessage(ChatColor.YELLOW + "Cart picks up items off the ground: "
                + Localization.boolStr(properties.get(this)));
    }

    @PropertyParser("pickup|pickupitems")
    public boolean parsePickupItems(PropertyParseContext<Boolean> context) {
        return context.inputBoolean();
    }

    @Override
    public boolean hasPermission(CommandSender sender, String name) {
        return Permission.PROPERTY_PICKUPITEMS.has(sender);
    }

    @Override
    public Boolean getDefault() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean getData(CartInternalData data) {
        return data.pickUpItems;
    }

    @Override
    public void setData(CartInternalData data, Boolean value) {
        data.pickUpItems = value.booleanValue();
    }

    @Override
    public Optional<Boolean> readFromConfig(ConfigurationNode config) {
        return Util.getConfigOptional(config, "pickUp", boolean.class);
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Boolean> value) {
        Util.setConfigOptional(config, "pickUp", value);
    }
}
