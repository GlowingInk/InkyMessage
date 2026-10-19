package ink.glowing.text.symbolic;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Finder of symbolic styles by their symbol.
 */
public interface SymbolicStyleFinder {
    /**
     * Finder that never finds any symbolic style.
     */
    SymbolicStyleFinder EMPTY = (symbol) -> null;

    /**
     * Find symbolic style by its symbol.
     * @param symbol style symbol
     * @return found symbolic style
     */
    @Contract(pure = true)
    @Nullable SymbolicStyle findSymbolicStyle(char symbol);

    /**
     * Creates a finder that uses this finder first, and the provided one if nothing was found.
     * @param symbolicStyleFinder finder to use if this one found nothing
     * @return the composed finder
     */
    default @NotNull SymbolicStyleFinder thenSymbolicStyleFinder(@NotNull SymbolicStyleFinder symbolicStyleFinder) {
        return (label) -> {
            var symbolic = findSymbolicStyle(label);
            return symbolic != null ? symbolic : symbolicStyleFinder.findSymbolicStyle(label);
        };
    }

    /**
     * Creates a finder of the provided symbolic styles.
     * If several symbolic styles share the same symbol, the last one is used.
     * @param symbolicStyles symbolic styles to find from
     * @return the finder
     */
    static @NotNull SymbolicStyleFinder symbolicStyleFinder(@NotNull SymbolicStyle @NotNull ... symbolicStyles) {
        return symbolicStyleFinder(Arrays.asList(symbolicStyles));
    }

    /**
     * Creates a finder of the provided symbolic styles.
     * If several symbolic styles share the same symbol, the last one is used.
     * @param symbolicStyles symbolic styles to find from
     * @return the finder
     */
    static @NotNull SymbolicStyleFinder symbolicStyleFinder(@NotNull SequencedCollection<? extends @NotNull SymbolicStyle> symbolicStyles) {
        return switch (symbolicStyles.size()) {
            case 0 -> EMPTY;
            case 1 -> symbolicStyles.getFirst();
            default -> symbolicStyleFinder((Collection<? extends SymbolicStyle>) symbolicStyles);
        };
    }

    /**
     * Creates a finder of the provided symbolic styles.
     * If several symbolic styles share the same symbol, the last one is used.
     * @param symbolicStyles symbolic styles to find from
     * @return the finder
     */
    static @NotNull SymbolicStyleFinder symbolicStyleFinder(@NotNull Collection<? extends @NotNull SymbolicStyle> symbolicStyles) {
        Map<Character, SymbolicStyle> symbolicStylesMap = new HashMap<>(symbolicStyles.size());
        for (var symbolicStyle : symbolicStyles) symbolicStylesMap.put(symbolicStyle.symbol(), symbolicStyle);
        return symbolicStylesMap::get;
    }

    /**
     * Creates a finder of the provided symbolic styles.
     * If several symbolic styles share the same symbol, the last one is used.
     * @param symbolicStyles symbolic styles to find from
     * @return the finder
     */
    static @NotNull SymbolicStyleFinder symbolicStyleFinder(@NotNull Iterable<? extends @NotNull SymbolicStyle> symbolicStyles) {
        Map<Character, SymbolicStyle> symbolicStylesMap = new HashMap<>();
        for (var symbolicStyle : symbolicStyles) symbolicStylesMap.put(symbolicStyle.symbol(), symbolicStyle);
        return symbolicStylesMap::get;
    }

    /**
     * Creates a finder backed by the provided map.
     * The map is not copied, so its later changes are reflected by the finder.
     * @param symbolicStylesMap map of symbolic styles by their symbol
     * @return the finder
     */
    static @NotNull SymbolicStyleFinder symbolicStyleFinder(@NotNull Map<Character, SymbolicStyle> symbolicStylesMap) {
        return symbolicStylesMap::get;
    }

    /**
     * Composes finders into one, querying them in the provided order.
     * @param symbolicStyleFinders finders to compose
     * @return the composed finder
     */
    static @NotNull SymbolicStyleFinder composeSymbolicStyleFinders(@NotNull SymbolicStyleFinder @NotNull ... symbolicStyleFinders) {
        return composeSymbolicStyleFinders(Arrays.asList(symbolicStyleFinders));
    }

    /**
     * Composes finders into one, querying them in the provided order.
     * @param symbolicStyleFinders finders to compose
     * @return the composed finder
     */
    static @NotNull SymbolicStyleFinder composeSymbolicStyleFinders(@NotNull Iterable<? extends @NotNull SymbolicStyleFinder> symbolicStyleFinders) {
        var iterator = symbolicStyleFinders.iterator();
        if (!iterator.hasNext()) return EMPTY;
        SymbolicStyleFinder result = iterator.next();
        do {
            result = result.thenSymbolicStyleFinder(iterator.next());
        } while (iterator.hasNext());
        return result;
    }
}
