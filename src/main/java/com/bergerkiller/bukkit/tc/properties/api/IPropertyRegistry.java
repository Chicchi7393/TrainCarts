package com.bergerkiller.bukkit.tc.properties.api;

import java.util.Collection;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.properties.IProperties;

/**
 * Registers and tracks all possible cart and train properties.
 * All built-in properties are registered, other plugins can register
 * their own properties as well.
 */
public interface IPropertyRegistry {

    /**
     * Looks up a previously registered property by name.
     * Parsers defined using {@link PropertyParser} are matched against
     * this name, and the property of the parser that matches is returned.
     * Returns null if a property with parser by this name does not exist.
     * 
     * @param <T> Type of property value
     * @param name Name of the property to find
     * @return property matching this name, null if none is registered
     */
    <T> IProperty<T> find(String name);

    /**
     * Looks up a previously registered property by name, then attempts
     * to parse the input value to a value for that property.
     * To check whether parsing succeeded, use
     * {@link PropertyParseResult#isSuccessful()}. If parsing failed, the
     * reason for failing can be found using {@link PropertyParseResult#getReason()}.
     * 
     * @param <T> Type of property value
     * @param properties The properties the value is parsed for, can be train or cart
     *        properties. The previous (current) value is read from these properties
     *        before parsing. This is used in case a parser makes use of the current
     *        value to parse a value, for example, when adding elements to a list.
     *        If <i>null</i> is provided, the default value of the property is used.
     * @param name Name of the property to parse, matches with {@link PropertyParser}
     * @param input Input value to parse using the property parser, if found
     * @return result of parsing the property by this name using the value
     */
    <T> PropertyParseResult<T> parse(IProperties properties, String name, String input);

    /**
     * Gets an unmodifiable list of all registered properties
     * 
     * @return unmodifiable list of all properties
     */
    Collection<IProperty<Object>> all();

    /**
     * Registers a new property
     * 
     * @param property The property to register
     */
    void register(IProperty<?> property);

    /**
     * Undoes previous registration of a property. This makes the
     * property unavailable again. Configuration data that stores
     * this property is not deleted.<br>
     * <br>
     * <b>Must be the exact same instance as used in {@link #register(IProperty)}</b>
     * 
     * @param property The property to unregister
     */
    void unregister(IProperty<?> property);

    /**
     * Registers all properties statically defined in a class, or all the
     * enum constants of an enumeration.
     * 
     * @param propertiesClass Class holding the static properties or enum constants
     * @see #register(IProperty)
     */
    default void registerAll(Class<?> propertiesClass) {
        for (IProperty<?> property : CommonUtil.getClassConstants(propertiesClass, IProperty.class)) {
            register(property);
        }
    }

    /**
     * Undoes previous registration of all properties statically defined in a class, or all the
     * enum constants of an enumeration.
     * 
     * @param propertiesClass Class holding the static properties or enum constants
     * @see #unregister(IProperty)
     */
    default void unregisterAll(Class<?> propertiesClass) {
        for (IProperty<?> property : CommonUtil.getClassConstants(propertiesClass, IProperty.class)) {
            unregister(property);
        }
    }

    /**
     * Convenience function, same as {@link TrainCarts#getPropertyRegistry()}.
     * 
     * @return property registry instance
     * @see TrainCarts#getPropertyRegistry()
     */
    public static IPropertyRegistry instance() {
        return TrainCarts.plugin.getPropertyRegistry();
    }
}
