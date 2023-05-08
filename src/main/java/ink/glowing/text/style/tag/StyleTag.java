package ink.glowing.text.style.tag;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.utils.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static ink.glowing.text.InkyMessage.inkyMessage;

@ApiStatus.OverrideOnly
public sealed interface StyleTag<T> extends Named permits StyleTag.Complex, StyleTag.Plain {
    @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull T value);

    @NotNull @Unmodifiable List<String> read(@NotNull InkyMessage.Resolver resolver, @NotNull Component text);

    @ApiStatus.OverrideOnly
    non-sealed interface Plain extends StyleTag<String> {}

    @ApiStatus.OverrideOnly
    non-sealed interface Complex extends StyleTag<Component> {}

    default @NotNull String asFormatted(@NotNull String param, @NotNull String value) {
        String result = name();
        if (!param.isEmpty()) result += ":" + param;
        if (!value.isEmpty()) result += " " + value;
        return "(" + result + ")";
    }

    default @NotNull String asFormatted(@NotNull String param, @NotNull Component value, @NotNull InkyMessage.Resolver resolver) {
        return asFormatted(param, inkyMessage().serialize(value, resolver));
    }
}
