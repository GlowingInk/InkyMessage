package ink.glowing.text.modifier.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

enum FontModifier implements Modifier.Plain {
    INSTANCE;

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
        //noinspection PatternValidation
        return Key.parseable(param) ? text.font(Key.key(param)) : text;
    }

    @Override
    public @NotNull @Unmodifiable List<String> readModifier(@NotNull Component text, @NotNull InkyMessage inkyMessage) {
        return text.font() == null
                ? List.of()
                : List.of(asFormatted(text.font().asString(), ""));
    }

    @Override
    public @NotNull String asFormatted(@NotNull String param, @NotNull String value) {
        return label() + ":" + param;
    }

    @Override
    public @NotNull @LabelPattern String label() {
        return "font";
    }
}
