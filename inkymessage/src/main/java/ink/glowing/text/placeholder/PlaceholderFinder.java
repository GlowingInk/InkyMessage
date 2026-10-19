package ink.glowing.text.placeholder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Finder of placeholders by their label.
 */
@FunctionalInterface
public interface PlaceholderFinder {
    /**
     * Finder that never finds any placeholder.
     */
    PlaceholderFinder EMPTY = (_) -> null;

    /**
     * Finds placeholder by its label.
     * @param label placeholder label
     * @return found placeholder, or {@code null} if none
     */
    @Contract(pure = true)
    @Nullable Placeholder findPlaceholder(@NotNull String label);

    /**
     * Creates a finder that uses this finder first, and the provided one if nothing was found.
     * @param placeholderFinder finder to use if this one found nothing
     * @return the composed finder
     */
    default @NotNull PlaceholderFinder thenPlaceholderFinder(@NotNull PlaceholderFinder placeholderFinder) {
        return (label) -> {
            var placeholder = findPlaceholder(label);
            return placeholder != null ? placeholder : placeholderFinder.findPlaceholder(label);
        };
    }

    /**
     * Creates a finder of the provided placeholders.
     * If several placeholders share the same label, the last one is used.
     * @param placeholders placeholders to find from
     * @return the finder
     */
    static @NotNull PlaceholderFinder placeholderFinder(@NotNull Placeholder @NotNull ... placeholders) {
        return placeholderFinder(Arrays.asList(placeholders));
    }

    /**
     * Creates a finder of the provided placeholders.
     * If several placeholders share the same label, the last one is used.
     * @param placeholders placeholders to find from
     * @return the finder
     */
    static @NotNull PlaceholderFinder placeholderFinder(@NotNull SequencedCollection<? extends @NotNull Placeholder> placeholders) {
        return switch (placeholders.size()) {
            case 0 -> EMPTY;
            case 1 -> placeholders.getFirst();
            default -> placeholderFinder((Collection<? extends Placeholder>) placeholders);
        };
    }

    /**
     * Creates a finder of the provided placeholders.
     * If several placeholders share the same label, the last one is used.
     * @param placeholders placeholders to find from
     * @return the finder
     */
    static @NotNull PlaceholderFinder placeholderFinder(@NotNull Collection<? extends @NotNull Placeholder> placeholders) {
        Map<String, Placeholder> placeholdersMap = new HashMap<>(placeholders.size());
        for (var placeholder : placeholders) placeholdersMap.put(placeholder.label(), placeholder);
        return placeholdersMap::get;
    }

    /**
     * Creates a finder backed by the provided map.
     * The map is not copied, so its later changes are reflected by the finder.
     * @param placeholdersMap map of placeholders by their label
     * @return the finder
     */
    static @NotNull PlaceholderFinder placeholderFinder(@NotNull Map<String, Placeholder> placeholdersMap) {
        return placeholdersMap::get;
    }

    /**
     * Creates a finder of the provided placeholders.
     * If several placeholders share the same label, the last one is used.
     * @param placeholders placeholders to find from
     * @return the finder
     */
    static @NotNull PlaceholderFinder placeholderFinder(@NotNull Iterable<? extends @NotNull Placeholder> placeholders) {
        Map<String, Placeholder> placeholdersMap = new HashMap<>();
        for (var placeholder : placeholders) placeholdersMap.put(placeholder.label(), placeholder);
        return placeholdersMap::get;
    }

    /**
     * Composes finders into one, querying them in the provided order.
     * @param placeholderFinders finders to compose
     * @return the composed finder
     */
    static @NotNull PlaceholderFinder composePlaceholderFinders(@NotNull PlaceholderFinder @NotNull ... placeholderFinders) {
        return composePlaceholderFinders(Arrays.asList(placeholderFinders));
    }

    /**
     * Composes finders into one, querying them in the provided order.
     * @param placeholderFinders finders to compose
     * @return the composed finder
     */
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