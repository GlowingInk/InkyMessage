package ink.glowing.text.style.modifier;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.utils.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static ink.glowing.text.InkyMessage.inkyMessage;

public sealed interface StyleModifier<T> extends Named, ModifierGetter permits StyleModifier.Complex, StyleModifier.Plain {
    @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull T value);

    @NotNull @Unmodifiable List<String> read(@NotNull InkyMessage.Resolver resolver, @NotNull Component text);

    @Override
    default StyleModifier<T> findModifier(@NotNull String name) {
        return name.equals(name()) ? this : null;
    }

    default @NotNull String asFormatted(@NotNull String param, @NotNull String value) {
        String result = name();
        if (!param.isEmpty()) result += ":" + param;
        if (!value.isEmpty()) result += " " + value;
        return "(" + result + ")";
    }

    default @NotNull String asFormatted(@NotNull String param, @NotNull Component value, @NotNull InkyMessage.Resolver resolver) {
        return asFormatted(param, inkyMessage().serialize(value, resolver));
    }

    non-sealed interface Plain extends StyleModifier<String> {}

    non-sealed interface Complex extends StyleModifier<Component> {}
}
