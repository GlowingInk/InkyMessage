package ink.glowing.text.placeholders;

import ink.glowing.text.style.tag.StyleTag;
import net.kyori.adventure.key.Namespaced;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Placeholder extends Namespaced {
    @NotNull Component parse(@NotNull String value);

    default @Nullable StyleTag<?> getLocalTag(@NotNull String namespace) {
        return null;
    }
}
