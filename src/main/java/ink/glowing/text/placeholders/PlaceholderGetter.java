package ink.glowing.text.placeholders;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface PlaceholderGetter {
    @Nullable Placeholder findPlaceholder(@NotNull String name);

    @Contract(pure = true)
    default @NotNull PlaceholderGetter composePlaceholder(@NotNull PlaceholderGetter other) {
        return (name) -> {
            var placeholder = findPlaceholder(name);
            return placeholder == null ? other.findPlaceholder(name) : placeholder;
        };
    }

    @Contract(pure = true)
    default @NotNull PlaceholderGetter composePlaceholder(@NotNull PlaceholderGetter... others) {
        PlaceholderGetter result = this;
        for (var other : others) result = result.composePlaceholder((name) -> {
            var placeholder = findPlaceholder(name);
            return placeholder == null ? other.findPlaceholder(name) : placeholder;
        });
        return result;
    }

    static @NotNull PlaceholderGetter placeholderGetter(@NotNull Placeholder placeholder) {
        return (name) -> placeholder.name().equals(name) ? placeholder : null;
    }

    static @NotNull PlaceholderGetter placeholderGetter(@NotNull Placeholder @NotNull ... placeholders) {
        return switch (placeholders.length) {
            case 0 -> (name) -> null;
            case 1 -> placeholderGetter(placeholders[0]);
            default -> placeholderGetter(Arrays.asList(placeholders));
        };
    }

    static @NotNull PlaceholderGetter placeholderGetter(@NotNull Iterable<Placeholder> placeholders) {
        Map<String, Placeholder> placeholdersMap = new HashMap<>();
        for (var placeholder : placeholders) placeholdersMap.put(placeholder.name(), placeholder);
        return placeholdersMap::get;
    }
}