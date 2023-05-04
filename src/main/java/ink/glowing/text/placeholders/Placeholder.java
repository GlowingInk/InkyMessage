package ink.glowing.text.placeholders;

import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.utils.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@ApiStatus.OverrideOnly
public interface Placeholder extends Named {
    @NotNull Component parse(@NotNull String value);

    default @Nullable StyleTag<?> getLocalTag(@NotNull String tagName) {
        return null;
    }

    static @NotNull Placeholder placeholder(@NotNull String name, @NotNull String result) {
        return placeholder(name, Component.text(result));
    }

    static @NotNull Placeholder placeholder(@NotNull String name, @NotNull Component result) {
        return placeholder(name, (v) -> result);
    }

    static @NotNull Placeholder placeholder(
            @NotNull String name,
            @NotNull Function<@NotNull String, @NotNull Component> resultFunct
    ) {
        return new SimplePlaceholder(name, resultFunct);
    }
}
