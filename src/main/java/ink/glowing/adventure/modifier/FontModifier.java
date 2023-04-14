package ink.glowing.adventure.modifier;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public enum FontModifier implements Modifier {
    INSTANCE;

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        //noinspection PatternValidation
        return Key.parseable(param) ? text.font(Key.key(param)) : text;
    }

    @Override
    public @NotNull String namespace() {
        return "font";
    }
}
