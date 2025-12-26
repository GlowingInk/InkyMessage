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

public interface Modifier extends Ink, Labeled, ModifierFinder {
    @NotNull UnaryOperator<Component> prepareModification(@NotNull Modifier.Arguments arguments, @NotNull Context context);

    default @NotNull @Unmodifiable List<String> readModifier(@NotNull Component text, @NotNull InkyMessage inkyMessage) { // TODO Return Arguments too?
        return List.of();
    }

    @Override
    @ApiStatus.NonExtendable
    default Modifier findModifier(@NotNull String label) {
        return label.equals(label()) ? this : null;
    }

    default @NotNull ArgumentValue.Type unknownArgumentType(@NotNull String parameter) {
        return ArgumentValue.Type.STRING;
    }

    @ApiStatus.Internal
    default @NotNull String asFormatted(@NotNull String param, @NotNull String value) {
        StringBuilder result = new StringBuilder();
        result.append('(').append(label());
        if (!param.isEmpty()) result.append(':').append(param);
        if (!value.isEmpty()) result.append(' ').append(value);
        result.append(')');
        return result.toString();
    }

    @ApiStatus.Internal
    default @NotNull String asFormatted(@NotNull String param, @NotNull Component value, @NotNull InkyMessage inkyMessage) {
        return asFormatted(param, inkyMessage.serialize(value));
    }

    @Deprecated
    interface Plain extends Modifier {
        @Override
        default @NotNull UnaryOperator<Component> prepareModification(@NotNull Modifier.Arguments arguments, @NotNull Context context) {
            return (text) -> modify(text, arguments.parameter(), arguments.get(0).asString());
        }

        @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value);

        @Override
        default ArgumentValue.@NotNull Type unknownArgumentType(@NotNull String parameter) {
            return ArgumentValue.Type.STRING;
        }
    }

    @Deprecated
    interface Complex extends Modifier {
        @Override
        default @NotNull UnaryOperator<Component> prepareModification(@NotNull Modifier.Arguments arguments, @NotNull Context context) {
            return (text) -> modify(text, arguments.parameter(), arguments.get(0).asComponent());
        }

        @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value);

        @Override
        default ArgumentValue.@NotNull Type unknownArgumentType(@NotNull String parameter) {
            return ArgumentValue.Type.COMPONENT;
        }
    }

    interface Arguments {
        @Contract(pure = true)
        static @NotNull Arguments empty() {
            return ArgumentsImpl.EmptyArguments.INSTANCE;
        }

        @Contract(value = "_ -> new", pure = true)
        static @NotNull Arguments arguments(@NotNull String parameter) {
            return new ArgumentsImpl.ParameterArguments(parameter);
        }

        @Contract(value = "_ -> new", pure = true)
        static @NotNull Arguments arguments(@NotNull List<@NotNull ArgumentValue> arguments) {
            return arguments("", arguments);
        }

        @Contract(value = "_, _ -> new", pure = true)
        static @NotNull Arguments arguments(@NotNull String parameter, @NotNull List<@NotNull ArgumentValue> arguments) {
            return new ArgumentsImpl.ListedArguments(parameter, unmodifiableList(arguments));
        }

        @Contract(pure = true)
        @NotNull String parameter();

        @Contract(pure = true)
        @Unmodifiable @NotNull List<@NotNull ArgumentValue> list();

        default @NotNull Modifier.ArgumentValue get(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
            var list = list();
            return list.size() > index
                    ? list.get(index)
                    : ArgumentValue.empty();
        }
    }

    interface ArgumentValue {
        @Contract(pure = true)
        static @NotNull ArgumentValue empty() {
            return ArgumentValueImpl.EmptyArgument.INSTANCE;
        }

        @Contract(value = "_ -> new", pure = true)
        static @NotNull ArgumentValue argumentValue(@NotNull String value) {
            return new ArgumentValueImpl.StringArgument(value);
        }

        @Contract(value = "_ -> new", pure = true)
        static @NotNull ArgumentValue argumentValue(@NotNull Component value) {
            return new ArgumentValueImpl.ComponentArgument(value);
        }

        @Contract(pure = true)
        @NotNull Component asComponent();

        @Contract(pure = true)
        @NotNull String asString();

        default <T> @UnknownNullability T as(@NotNull Function<@NotNull String, T> funct) {
            return funct.apply(asString());
        }

        @Contract(pure = true)
        default <T extends Enum<T>> @Nullable T as(@NotNull Class<T> enumClass) {
            return as(enumClass, null);
        }

        @Contract(value = "_, !null -> !null", pure = true)
        default <T extends Enum<T>> @Nullable T as(@NotNull Class<T> enumClass, @Nullable T def) {
            try {
                return Enum.valueOf(enumClass, asString().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                return def;
            }
        }

        @Contract(pure = true)
        default @NotNull TriState asTriState() {
            return switch (asString().toLowerCase(Locale.ROOT)) {
                case "true", "t", "yes", "y", "1", "1.0", "on", "yep" -> TriState.TRUE;
                case "false", "f", "no", "n", "0", "0.0", "off", "nope" -> TriState.FALSE;
                default -> TriState.NOT_SET;
            };
        }

        @Contract(pure = true)
        default boolean asBoolean(boolean def) {
            return asTriState().toBooleanOrElse(def);
        }

        @Contract(pure = true)
        default @NotNull OptionalInt asInt() {
            try {
                return OptionalInt.of(Integer.parseInt(asString()));
            } catch (NumberFormatException ex) {
                return OptionalInt.empty();
            }
        }

        @Contract(pure = true)
        default int asInt(int def) {
            try {
                return Integer.parseInt(asString());
            } catch (NumberFormatException ex) {
                return def;
            }
        }

        @Contract(pure = true)
        default @NotNull OptionalLong asLong() {
            try {
                return OptionalLong.of(Long.parseLong(asString()));
            } catch (NumberFormatException ex) {
                return OptionalLong.empty();
            }
        }

        @Contract(pure = true)
        default long asLong(long def) {
            try {
                return Long.parseLong(asString());
            } catch (NumberFormatException ex) {
                return def;
            }
        }

        @Contract(pure = true)
        default @NotNull OptionalFloat asFloat() {
            try {
                return OptionalFloat.of(Float.parseFloat(asString()));
            } catch (NumberFormatException ex) {
                return OptionalFloat.empty();
            }
        }

        @Contract(pure = true)
        default float asFloat(float def) {
            try {
                return Float.parseFloat(asString());
            } catch (NumberFormatException ex) {
                return def;
            }
        }

        @Contract(pure = true)
        default @NotNull OptionalDouble asDouble() {
            try {
                return OptionalDouble.of(Double.parseDouble(asString()));
            } catch (NumberFormatException ex) {
                return OptionalDouble.empty();
            }
        }

        @Contract(pure = true)
        default double asDouble(double def) {
            try {
                return Double.parseDouble(asString());
            } catch (NumberFormatException ex) {
                return def;
            }
        }

        enum Type {
            STRING, COMPONENT
        }
    }
}
