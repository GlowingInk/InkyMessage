package ink.glowing.text.style.tag;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface TagGetter {
    @Nullable StyleTag<?> findTag(@NotNull String name);

    default @NotNull TagGetter composeTag(@NotNull TagGetter other) {
        return (name) -> {
            var tag = findTag(name);
            return tag == null ? other.findTag(name) : tag;
        };
    }

    @Contract(pure = true)
    default @NotNull TagGetter composeTag(@NotNull TagGetter... others) {
        TagGetter result = this;
        for (var other : others) result = result.composeTag((name) -> {
            var tag = findTag(name);
            return tag == null ? other.findTag(name) : tag;
        });
        return result;
    }

    static @NotNull TagGetter tagGetter(@NotNull StyleTag<?> tag) {
        return (name) -> tag.name().equals(name) ? tag : null;
    }

    static @NotNull TagGetter tagGetter(@NotNull StyleTag<?> @NotNull ... tags) {
        return switch (tags.length) {
            case 0 -> (name) -> null;
            case 1 -> tagGetter(tags[0]);
            default -> tagGetter(Arrays.asList(tags));
        };
    }

    static @NotNull TagGetter tagGetter(@NotNull Iterable<StyleTag<?>> tags) {
        Map<String, StyleTag<?>> tagsMap = new HashMap<>();
        for (var tag : tags) tagsMap.put(tag.name(), tag);
        return tagsMap::get;
    }
}
