package ink.glowing.text.symbolic;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface SymbolicStyleFinder {
    /**
     * Find symbolic style by its symbol.
     * @param symbol style symbol
     * @return found symbolic style
     */
    @Contract(pure = true)
    @Nullable SymbolicStyle findSymbolicStyle(char symbol);

    default @NotNull SymbolicStyleFinder thenSymbloicStyleFinder(@NotNull SymbolicStyleFinder symbolicStyleFinder) {
        return (name) -> {
            var symbolic = findSymbolicStyle(name);
            return symbolic != null ? symbolic : symbolicStyleFinder.findSymbolicStyle(name);
        };
    }

    static @NotNull SymbolicStyleFinder symbolicStyleFinder(@NotNull SymbolicStyle @NotNull ... symbolicStyles) {
        return symbolicStyleFinder(Arrays.asList(symbolicStyles));
    }

    static @NotNull SymbolicStyleFinder symbolicStyleFinder(@NotNull SequencedCollection<@NotNull SymbolicStyle> symbolicStyles) {
        return switch (symbolicStyles.size()) {
            case 0 -> (symbol) -> null;
            case 1 -> symbolicStyles.getFirst();
            default -> symbolicStyleFinder((Collection<? extends SymbolicStyle>) symbolicStyles);
        };
    }

    static @NotNull SymbolicStyleFinder symbolicStyleFinder(@NotNull Collection<? extends @NotNull SymbolicStyle> symbolicStyles) {
        Map<Character, SymbolicStyle> symbolicStylesMap = new HashMap<>(symbolicStyles.size());
        for (var symbolicStyle : symbolicStyles) symbolicStylesMap.put(symbolicStyle.symbol(), symbolicStyle);
        return symbolicStylesMap::get;
    }

    static @NotNull SymbolicStyleFinder symbolicStyleFinder(@NotNull Iterable<? extends @NotNull SymbolicStyle> symbolicStyles) {
        Map<Character, SymbolicStyle> symbolicStylesMap = new HashMap<>();
        for (var symbolicStyle : symbolicStyles) symbolicStylesMap.put(symbolicStyle.symbol(), symbolicStyle);
        return symbolicStylesMap::get;
    }

    static @NotNull SymbolicStyleFinder symbolicStyleFinder(@NotNull Map<Character, SymbolicStyle> symbolicStylesMap) {
        return symbolicStylesMap::get;
    }

    static @NotNull SymbolicStyleFinder composeSymbolicStyleFinders(@NotNull SymbolicStyleFinder @NotNull ... symbolicStyleFinders) {
        return composeSymbolicStyleFinders(Arrays.asList(symbolicStyleFinders));
    }

    static @NotNull SymbolicStyleFinder composeSymbolicStyleFinders(@NotNull Iterable<? extends @NotNull SymbolicStyleFinder> symbolicStyleFinders) {
        var iterator = symbolicStyleFinders.iterator();
        if (!iterator.hasNext()) return (symbol) -> null;
        SymbolicStyleFinder result = iterator.next();
        do {
            result = result.thenSymbloicStyleFinder(iterator.next());
        } while (iterator.hasNext());
        return result;
    }
}
