package ink.glowing.text.placeholders;

import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.style.tag.TagGetter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

record PlaceholderImpl(
        @NotNull String name,
        @NotNull Function<@NotNull String, @NotNull Component> resultFunct,
        @NotNull TagGetter tagGetter
) implements Placeholder {
    @Override
    public @NotNull Component parse(@NotNull String value) {
        return resultFunct.apply(value);
    }

    @Override
    public @Nullable StyleTag<?> findTag(@NotNull String tagName) {
        return tagGetter.findTag(tagName);
    }

    @Override
    public @Nullable Placeholder findPlaceholder(@NotNull String name) {
        return name().equals(name) ? this : null;
    }
}
