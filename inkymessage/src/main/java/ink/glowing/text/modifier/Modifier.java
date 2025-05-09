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

    default boolean unknownArgumentAsString(@NotNull String parameter) {
        return true;
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
        default boolean unknownArgumentAsString(@NotNull String parameter) {
            return false;
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
        default boolean unknownArgumentAsString(@NotNull String parameter) {
            return true;
        }
    }

    interface Arguments {
        static @NotNull Arguments empty() {
            return ArgumentsImpl.EmptyArguments.INSTANCE;
        }

        static @NotNull Arguments arguments(@NotNull String parameter) {
            return new ArgumentsImpl.ParameterArguments(parameter);
        }

        static @NotNull Arguments arguments(@NotNull List<@NotNull ArgumentValue> arguments) {
            return arguments("", arguments);
        }

        static @NotNull Arguments arguments(@NotNull String parameter, @NotNull List<@NotNull ArgumentValue> arguments) {
            return new ArgumentsImpl.ListedArguments(parameter, unmodifiableList(arguments));
        }

        @NotNull String parameter();

        @Unmodifiable @NotNull List<@NotNull ArgumentValue> list();

        default @NotNull Modifier.ArgumentValue get(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
            var list = list();
            return list.size() > index
                    ? list.get(index)
                    : ArgumentValue.empty();
        }
    }

    interface ArgumentValue {
        static @NotNull ArgumentValue empty() {
            return ArgumentValueImpl.EmptyArgument.INSTANCE;
        }

        static @NotNull ArgumentValue argumentValue(@NotNull String value) {
            return new ArgumentValueImpl.StringArgument(value);
        }

        static @NotNull ArgumentValue argumentValue(@NotNull Component value) {
            return new ArgumentValueImpl.ComponentArgument(value);
        }

        @NotNull Component asComponent();

        @NotNull String asString();

        default <T> @UnknownNullability T as(@NotNull Function<@NotNull String, T> funct) {
            return funct.apply(asString());
        }

        default @NotNull TriState asTriState() {
            return as((string) -> switch (string.toLowerCase(Locale.ROOT)) {
                case "true", "t", "yes", "y", "1", "1.0", "on", "yep" -> TriState.TRUE;
                case "false", "f", "no", "n", "0", "0.0", "off", "nope" -> TriState.FALSE;
                default -> TriState.NOT_SET;
            });
        }

        default @NotNull OptionalInt asInt() {
            return as(string -> {
                try {
                    return OptionalInt.of(Integer.parseInt(string));
                } catch (NumberFormatException ex) {
                    return OptionalInt.empty();
                }
            });
        }

        default @NotNull OptionalLong asLong() {
            return as(string -> {
                try {
                    return OptionalLong.of(Long.parseLong(string));
                } catch (NumberFormatException ex) {
                    return OptionalLong.empty();
                }
            });
        }

        default @NotNull OptionalFloat asFloat() {
            return as(string -> {
                try {
                    return OptionalFloat.of(Float.parseFloat(string));
                } catch (NumberFormatException ex) {
                    return OptionalFloat.empty();
                }
            });
        }

        default @NotNull OptionalDouble asDouble() {
            return as(string -> {
                try {
                    return OptionalDouble.of(Double.parseDouble(string));
                } catch (NumberFormatException ex) {
                    return OptionalDouble.empty();
                }
            });
        }
    }
}
