package ink.glowing.text.placeholder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@FunctionalInterface
public interface PlaceholderGetter {
    @Nullable Placeholder findPlaceholder(@NotNull String name);

    static @NotNull PlaceholderGetter placeholderGetter(@NotNull Placeholder placeholder) {
        return (name) -> placeholder.name().equals(name) ? placeholder : null;
    }

    static @NotNull PlaceholderGetter placeholderGetter(@NotNull Placeholder @NotNull ... placeholders) {
        return placeholderGetter(Arrays.asList(placeholders));
    }

    static @NotNull PlaceholderGetter placeholderGetter(@NotNull SequencedCollection<@NotNull Placeholder> placeholders) {
        return switch (placeholders.size()) {
            case 0 -> (name) -> null;
            case 1 -> placeholderGetter(placeholders.getFirst());
            default -> placeholderGetter((Collection<? extends Placeholder>) placeholders);
        };
    }

    static @NotNull PlaceholderGetter placeholderGetter(@NotNull Collection<? extends @NotNull Placeholder> placeholders) {
        Map<String, Placeholder> placeholdersMap = new HashMap<>(placeholders.size());
        for (var placeholder : placeholders) placeholdersMap.put(placeholder.name(), placeholder);
        return placeholdersMap::get;
    }

    static @NotNull PlaceholderGetter placeholderGetter(@NotNull Iterable<? extends @NotNull Placeholder> placeholders) {
        Map<String, Placeholder> placeholdersMap = new HashMap<>();
        for (var placeholder : placeholders) placeholdersMap.put(placeholder.name(), placeholder);
        return placeholdersMap::get;
    }

    static @NotNull PlaceholderGetter composePlaceholderGetters(@NotNull PlaceholderGetter @NotNull ... placeholderGetters) {
        return composePlaceholderGetters(Arrays.asList(placeholderGetters));
    }

    static @NotNull PlaceholderGetter composePlaceholderGetters(@NotNull Iterable<? extends @NotNull PlaceholderGetter> placeholderGetters) {
        var iterator = placeholderGetters.iterator();
        PlaceholderGetter result = (name) -> null;
        while (iterator.hasNext()) {
            PlaceholderGetter last = result;
            PlaceholderGetter next = iterator.next();
            result = (name) -> {
                Placeholder placeholder = next.findPlaceholder(name);
                return placeholder == null ? last.findPlaceholder(name) : placeholder;
            };
        }
        return result;
    }
}