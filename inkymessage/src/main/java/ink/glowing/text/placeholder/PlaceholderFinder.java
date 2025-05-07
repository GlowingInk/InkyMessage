package ink.glowing.text.placeholder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@FunctionalInterface
public interface PlaceholderFinder {
    PlaceholderFinder EMPTY = (label) -> null;

    @Contract(pure = true)
    @Nullable Placeholder findPlaceholder(@NotNull String label);

    default @NotNull PlaceholderFinder thenPlaceholderFinder(@NotNull PlaceholderFinder placeholderFinder) {
        return (label) -> {
            var placeholder = findPlaceholder(label);
            return placeholder != null ? placeholder : placeholderFinder.findPlaceholder(label);
        };
    }

    static @NotNull PlaceholderFinder placeholderFinder(@NotNull Placeholder @NotNull ... placeholders) {
        return placeholderFinder(Arrays.asList(placeholders));
    }

    static @NotNull PlaceholderFinder placeholderFinder(@NotNull SequencedCollection<? extends @NotNull Placeholder> placeholders) {
        return switch (placeholders.size()) {
            case 0 -> EMPTY;
            case 1 -> placeholders.getFirst();
            default -> placeholderFinder((Collection<? extends Placeholder>) placeholders);
        };
    }

    static @NotNull PlaceholderFinder placeholderFinder(@NotNull Collection<? extends @NotNull Placeholder> placeholders) {
        Map<String, Placeholder> placeholdersMap = new HashMap<>(placeholders.size());
        for (var placeholder : placeholders) placeholdersMap.put(placeholder.label(), placeholder);
        return placeholdersMap::get;
    }

    static @NotNull PlaceholderFinder placeholderFinder(@NotNull Map<String, Placeholder> placeholdersMap) {
        return placeholdersMap::get;
    }

    static @NotNull PlaceholderFinder placeholderFinder(@NotNull Iterable<? extends @NotNull Placeholder> placeholders) {
        Map<String, Placeholder> placeholdersMap = new HashMap<>();
        for (var placeholder : placeholders) placeholdersMap.put(placeholder.label(), placeholder);
        return placeholdersMap::get;
    }

    static @NotNull PlaceholderFinder composePlaceholderFinders(@NotNull PlaceholderFinder @NotNull ... placeholderFinders) {
        return composePlaceholderFinders(Arrays.asList(placeholderFinders));
    }

    static @NotNull PlaceholderFinder composePlaceholderFinders(@NotNull Iterable<? extends @NotNull PlaceholderFinder> placeholderFinders) {
        var iterator = placeholderFinders.iterator();
        if (!iterator.hasNext()) return EMPTY;
        PlaceholderFinder result = iterator.next();
        do {
            result = result.thenPlaceholderFinder(iterator.next());
        } while (iterator.hasNext());
        return result;
    }
}