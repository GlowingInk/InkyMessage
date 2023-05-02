package ink.glowing.text.style.tag;

import ink.glowing.text.InkyMessage;
import net.kyori.adventure.key.Namespaced;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static ink.glowing.text.InkyMessage.inkyMessage;

public sealed interface StyleTag<T> extends Namespaced {
    @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull T value);

    @NotNull @Unmodifiable List<String> read(@NotNull InkyMessage.Resolver resolver, @NotNull Component text);

    non-sealed interface Plain extends StyleTag<String> {}

    non-sealed interface Complex extends StyleTag<Component> {}

    default @NotNull String asFormatted(@NotNull String param, @NotNull String value) {
        String result = namespace();
        if (!param.isEmpty()) result += ":" + param;
        if (!value.isEmpty()) result += " " + value;
        return "(" + result + ")";
    }

    default @NotNull String asFormatted(@NotNull String param, @NotNull Component value, @NotNull InkyMessage.Resolver resolver) {
        String result = namespace();
        if (!param.isEmpty()) result += ":" + param;
        if (!value.equals(Component.empty())) result += " " + inkyMessage().serialize(value, resolver);
        return "(" + result + ")";
    }
}
