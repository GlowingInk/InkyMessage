package ink.glowing.text.style.modifier.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.style.modifier.StyleModifier;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class FontModifier implements StyleModifier.Plain { private FontModifier() {}
    private static final FontModifier INSTANCE = new FontModifier();

    public static @NotNull StyleModifier.Plain fontModifier() {
        return INSTANCE;
    }

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
        //noinspection PatternValidation
        return Key.parseable(param) ? text.font(Key.key(param)) : text;
    }

    @Override
    public @NotNull @Unmodifiable List<String> read(@NotNull InkyMessage.Resolver resolver, @NotNull Component text) {
        return text.font() == null
                ? List.of()
                : List.of(asFormatted(text.font().asString(), ""));
    }

    @Override
    public @NotNull String asFormatted(@NotNull String param, @NotNull String value) {
        return name() + ":" + param;
    }

    @Override
    public @NotNull @NamePattern String name() {
        return "font";
    }
}
