package ink.glowing.text.modifier;

import ink.glowing.text.Ink;
import ink.glowing.text.InkyMessage;
import ink.glowing.text.utils.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.UnaryOperator;

public interface Modifier extends Ink, Named, ModifierFinder {
    @NotNull UnaryOperator<Component> prepareModify(@NotNull Modifier.Tokens parameters);

    default @NotNull @Unmodifiable List<String> read(@NotNull Component text, @NotNull InkyMessage inkyMessage) {
        return List.of();
    }

    @Override
    default Modifier findModifier(@NotNull String name) {
        return name.equals(name()) ? this : null;
    }

    @ApiStatus.Internal
    default @NotNull String asFormatted(@NotNull String param, @NotNull String value) {
        StringBuilder result = new StringBuilder();
        result.append('(').append(name());
        if (!param.isEmpty()) result.append(':').append(param);
        if (!value.isEmpty()) result.append(' ').append(value);
        result.append(')');
        return result.toString();
    }

    @ApiStatus.Internal
    default @NotNull String asFormatted(@NotNull String param, @NotNull Component value, @NotNull InkyMessage inkyMessage) {
        return asFormatted(param, inkyMessage.serialize(value));
    }

    interface Plain extends Modifier {
        @Override
        @NotNull
        default UnaryOperator<Component> prepareModify(@NotNull Modifier.Tokens input) {
            String value = input.remainingString();
            return (text) -> modify(text, input.parameter(), value);
        }

        @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value);
    }

    interface Complex extends Modifier {
        @Override
        @NotNull
        default UnaryOperator<Component> prepareModify(@NotNull Modifier.Tokens input) {
            Component value = input.remainingComponent();
            return (text) -> modify(text, input.parameter(), value);
        }

        @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value);
    }

    interface Tokens {
        @NotNull String parameter();

        @Nullable String nextString();
        @NotNull String remainingString();
        @Nullable Component nextComponent();
        @NotNull Component remainingComponent();

        boolean hasMore();
    }
}
