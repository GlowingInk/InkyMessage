package ink.glowing.text.modifier;

import ink.glowing.text.Context;
import ink.glowing.text.Ink;
import ink.glowing.text.InkyMessage;
import ink.glowing.text.utils.Labeled;
import ink.glowing.text.utils.OptionalFloat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static java.util.Collections.unmodifiableList;

/**
 * Ink that modifies the preceding text, written as {@code (label:parameter value)}.
 */
public non-sealed interface Modifier extends Ink, Labeled, ModifierFinder {
    /**
     * Prepares modification of text with the provided arguments.
     * @param arguments modifier arguments
     * @param context current context
     * @return function applying the modification to text
     */
    @NotNull UnaryOperator<Component> prepareModification(@NotNull Modifier.Arguments arguments, @NotNull Context context);

    /**
     * Reads modifiers that would recreate the text's style, used for serialization.
     * @param text text to read from
     * @param inkyMessage InkyMessage instance that is serializing the text
     * @return formatted modifiers
     */
    default @NotNull @Unmodifiable List<String> readModifier(@NotNull Component text, @NotNull InkyMessage inkyMessage) { // TODO Return Arguments too?
        return List.of();
    }

    @Override
    @ApiStatus.NonExtendable
    default @Nullable Modifier findModifier(@NotNull String label) {
        return label.equals(label()) ? this : null;
    }

    /**
     * Type of arguments that are not explicitly marked as plain {@code <...>} or rich {@code [...]}.
     * @param parameter modifier parameter
     * @return type of unmarked arguments
     */
    @NotNull ArgumentValue.Type unknownArgumentType(@NotNull String parameter);

    /**
     * Formats this modifier as a string.
     * @param param modifier parameter
     * @param value modifier value
     * @return formatted modifier
     */
    @ApiStatus.Internal
    default @NotNull String asFormatted(@NotNull String param, @NotNull String value) {
        StringBuilder result = new StringBuilder();
        result.append('(').append(label());
        if (!param.isEmpty()) result.append(':').append(param);
        if (!value.isEmpty()) result.append(' ').append(value);
        result.append(')');
        return result.toString();
    }

    /**
     * Formats this modifier as a string.
     * @param param modifier parameter
     * @param value modifier value, serialized with the provided InkyMessage
     * @param inkyMessage InkyMessage instance to serialize value with
     * @return formatted modifier
     */
    @ApiStatus.Internal
    default @NotNull String asFormatted(@NotNull String param, @NotNull Component value, @NotNull InkyMessage inkyMessage) {
        return asFormatted(param, inkyMessage.serialize(value));
    }

    /**
     * Parameter and argument values of a modifier.
     */
    interface Arguments {
        /**
         * Arguments without parameter and values.
         * @return empty arguments
         */
        @Contract(pure = true)
        static @NotNull Arguments empty() {
            return ArgumentsImpl.EmptyArguments.INSTANCE;
        }

        /**
         * Creates arguments with only parameter.
         * @param parameter modifier parameter
         * @return the arguments
         */
        @Contract(value = "_ -> new", pure = true)
        static @NotNull Arguments arguments(@NotNull String parameter) {
            return new ArgumentsImpl.ParameterArguments(parameter);
        }

        /**
         * Creates arguments with only values.
         * @param arguments argument values
         * @return the arguments
         */
        @Contract(value = "_ -> new", pure = true)
        static @NotNull Arguments arguments(@NotNull List<@NotNull ArgumentValue> arguments) {
            return arguments("", arguments);
        }

        /**
         * Creates arguments with parameter and values.
         * @param parameter modifier parameter
         * @param arguments argument values
         * @return the arguments
         */
        @Contract(value = "_, _ -> new", pure = true)
        static @NotNull Arguments arguments(@NotNull String parameter, @NotNull List<@NotNull ArgumentValue> arguments) {
            return new ArgumentsImpl.ListedArguments(parameter, unmodifiableList(arguments));
        }

        /**
         * Modifier parameter, written after the colon.
         * @return the parameter, or an empty string if none
         */
        @Contract(pure = true)
        @NotNull String parameter();

        /**
         * List of argument values.
         * @return argument values
         */
        @Contract(pure = true)
        @Unmodifiable @NotNull List<@NotNull ArgumentValue> list();

        /**
         * Gets argument value at the provided index.
         * @param index index of argument
         * @return argument value, or an empty value if there's none at the index
         */
        default @NotNull Modifier.ArgumentValue get(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
            var list = list();
            return list.size() > index
                    ? list.get(index)
                    : ArgumentValue.empty();
        }
    }

    /**
     * Value of a modifier argument, either plain or rich.
     */
    interface ArgumentValue {
        /**
         * Empty argument value.
         * @return empty value
         */
        @Contract(pure = true)
        static @NotNull ArgumentValue empty() {
            return ArgumentValueImpl.EmptyArgument.INSTANCE;
        }

        /**
         * Creates plain argument value.
         * @param value plain value
         * @return the argument value
         */
        @Contract(value = "_ -> new", pure = true)
        static @NotNull ArgumentValue argumentValue(@NotNull String value) {
            return new ArgumentValueImpl.StringArgument(value);
        }

        /**
         * Creates rich argument value.
         * @param value rich value
         * @return the argument value
         */
        @Contract(value = "_ -> new", pure = true)
        static @NotNull ArgumentValue argumentValue(@NotNull Component value) {
            return new ArgumentValueImpl.ComponentArgument(value);
        }

        /**
         * Gets value as a component.
         * @return rich value
         */
        @Contract(pure = true)
        @NotNull Component asComponent();

        /**
         * Gets value as a string. Rich values are flattened into plain text.
         * @return plain value
         */
        @Contract(pure = true)
        @NotNull String asString();

        /**
         * Converts plain value using the provided function.
         * @param funct conversion function
         * @param <T> type of result
         * @return converted value
         */
        default <T> @UnknownNullability T as(@NotNull Function<@NotNull String, T> funct) {
            return funct.apply(asString());
        }

        /**
         * Gets enum constant by plain value, ignoring case.
         * @param enumClass class of enum
         * @param <T> type of enum
         * @return enum constant, or {@code null} if none matches
         */
        @Contract(pure = true)
        default <T extends Enum<T>> @Nullable T as(@NotNull Class<T> enumClass) {
            return as(enumClass, null);
        }

        /**
         * Gets enum constant by plain value, ignoring case.
         * @param enumClass class of enum
         * @param def default value
         * @param <T> type of enum
         * @return enum constant, or {@code def} if none matches
         */
        @Contract(value = "_, !null -> !null", pure = true)
        default <T extends Enum<T>> @Nullable T as(@NotNull Class<T> enumClass, @Nullable T def) {
            try {
                return Enum.valueOf(enumClass, asString().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                return def;
            }
        }

        /**
         * Parses plain value as tri-state, accepting values like {@code true}, {@code yes}, {@code on} and {@code false}, {@code no}, {@code off}.
         * @return parsed value, or {@link TriState#NOT_SET} if unknown
         */
        @Contract(pure = true)
        default @NotNull TriState asTriState() {
            return switch (asString().toLowerCase(Locale.ROOT)) {
                case "true", "t", "yes", "y", "1", "1.0", "on", "yep" -> TriState.TRUE;
                case "false", "f", "no", "n", "0", "0.0", "off", "nope" -> TriState.FALSE;
                default -> TriState.NOT_SET;
            };
        }

        /**
         * Parses plain value as boolean.
         * @param def default value
         * @return parsed value, or {@code def} if unknown
         * @see #asTriState()
         */
        @Contract(pure = true)
        default boolean asBoolean(boolean def) {
            return asTriState().toBooleanOrElse(def);
        }

        /**
         * Parses plain value as {@code int}.
         * @return parsed value, or an empty optional if not a number
         */
        @Contract(pure = true)
        default @NotNull OptionalInt asInt() {
            try {
                return OptionalInt.of(Integer.parseInt(asString()));
            } catch (NumberFormatException ex) {
                return OptionalInt.empty();
            }
        }

        /**
         * Parses plain value as {@code int}.
         * @param def default value
         * @return parsed value, or {@code def} if not a number
         */
        @Contract(pure = true)
        default int asInt(int def) {
            try {
                return Integer.parseInt(asString());
            } catch (NumberFormatException ex) {
                return def;
            }
        }

        /**
         * Parses plain value as {@code long}.
         * @return parsed value, or an empty optional if not a number
         */
        @Contract(pure = true)
        default @NotNull OptionalLong asLong() {
            try {
                return OptionalLong.of(Long.parseLong(asString()));
            } catch (NumberFormatException ex) {
                return OptionalLong.empty();
            }
        }

        /**
         * Parses plain value as {@code long}.
         * @param def default value
         * @return parsed value, or {@code def} if not a number
         */
        @Contract(pure = true)
        default long asLong(long def) {
            try {
                return Long.parseLong(asString());
            } catch (NumberFormatException ex) {
                return def;
            }
        }

        /**
         * Parses plain value as {@code float}.
         * @return parsed value, or an empty optional if not a number
         */
        @Contract(pure = true)
        default @NotNull OptionalFloat asFloat() {
            try {
                return OptionalFloat.of(Float.parseFloat(asString()));
            } catch (NumberFormatException ex) {
                return OptionalFloat.empty();
            }
        }

        /**
         * Parses plain value as {@code float}.
         * @param def default value
         * @return parsed value, or {@code def} if not a number
         */
        @Contract(pure = true)
        default float asFloat(float def) {
            try {
                return Float.parseFloat(asString());
            } catch (NumberFormatException ex) {
                return def;
            }
        }

        /**
         * Parses plain value as {@code double}.
         * @return parsed value, or an empty optional if not a number
         */
        @Contract(pure = true)
        default @NotNull OptionalDouble asDouble() {
            try {
                return OptionalDouble.of(Double.parseDouble(asString()));
            } catch (NumberFormatException ex) {
                return OptionalDouble.empty();
            }
        }

        /**
         * Parses plain value as {@code double}.
         * @param def default value
         * @return parsed value, or {@code def} if not a number
         */
        @Contract(pure = true)
        default double asDouble(double def) {
            try {
                return Double.parseDouble(asString());
            } catch (NumberFormatException ex) {
                return def;
            }
        }

        /**
         * Type of argument value.
         */
        enum Type {
            /**
             * Plain value.
             */
            STRING,
            /**
             * Rich value.
             */
            COMPONENT
        }
    }
}
