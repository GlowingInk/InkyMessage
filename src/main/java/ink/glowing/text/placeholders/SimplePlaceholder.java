package ink.glowing.text.placeholders;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

record SimplePlaceholder(
        @NotNull String name,
        @NotNull Function<@NotNull String, @NotNull Component> resultFunct
) implements Placeholder {
    @Override
    public @NotNull Component parse(@NotNull String value) {
        return resultFunct.apply(value);
    }
}
