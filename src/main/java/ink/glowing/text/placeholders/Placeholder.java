package ink.glowing.text.placeholders;

import ink.glowing.text.style.tag.StyleTag;
import net.kyori.adventure.key.Namespaced;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@ApiStatus.OverrideOnly
public interface Placeholder extends Namespaced {
    @NotNull Component parse(@NotNull String value);

    default @Nullable StyleTag<?> getLocalTag(@NotNull String tagNamespace) {
        return null;
    }

    static @NotNull Placeholder placeholder(@NotNull String namespace, @NotNull String result) {
        return placeholder(namespace, Component.text(result));
    }

    static @NotNull Placeholder placeholder(@NotNull String namespace, @NotNull Component result) {
        return placeholder(namespace, (v) -> result);
    }

    static @NotNull Placeholder placeholder(
            @NotNull String namespace,
            @NotNull Function<@NotNull String, @NotNull Component> resultFunct
    ) {
        return new SimplePlaceholder(namespace, resultFunct);
    }
}
