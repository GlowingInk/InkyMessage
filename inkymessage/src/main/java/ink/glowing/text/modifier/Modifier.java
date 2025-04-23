package ink.glowing.text.modifier;

import ink.glowing.text.Ink;
import ink.glowing.text.InkyMessage;
import ink.glowing.text.utils.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public sealed interface Modifier<T> extends Ink, Named, ModifierFinder permits Modifier.Complex, Modifier.Plain {
    @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull T value);

    default @NotNull @Unmodifiable List<String> read(@NotNull Component text, @NotNull InkyMessage inkyMessage) {
        return List.of();
    }

    @Override
    default Modifier<T> findModifier(@NotNull String name) {
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

    non-sealed interface Plain extends Modifier<String> {}

    non-sealed interface Complex extends Modifier<Component> {
        @ApiStatus.Internal
        default @NotNull String asFormatted(@NotNull String param, @NotNull Component value, @NotNull InkyMessage inkyMessage) {
            return asFormatted(param, inkyMessage.serialize(value));
        }
    }
}
