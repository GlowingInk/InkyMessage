package ink.glowing.text.modifier;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Finder of modifiers by their label.
 */
@FunctionalInterface
public interface ModifierFinder {
    /**
     * Finder that never finds any modifier.
     */
    ModifierFinder EMPTY = (_) -> null;

    /**
     * Finds modifier by its label.
     * @param label modifier label
     * @return found modifier, or {@code null} if none
     */
    @Contract(pure = true)
    @Nullable Modifier findModifier(@NotNull String label);

    /**
     * Creates a finder that uses this finder first, and the provided one if nothing was found.
     * @param modifierFinder finder to use if this one found nothing
     * @return the composed finder
     */
    default @NotNull ModifierFinder thenModifierFinder(@NotNull ModifierFinder modifierFinder) {
        return (label) -> {
            var modifier = findModifier(label);
            return modifier != null ? modifier : modifierFinder.findModifier(label);
        };
    }

    /**
     * Creates a finder of the provided modifiers.
     * If several modifiers share the same label, the last one is used.
     * @param modifiers modifiers to find from
     * @return the finder
     */
    static @NotNull ModifierFinder modifierFinder(@NotNull Modifier @NotNull ... modifiers) {
        return modifierFinder(Arrays.asList(modifiers));
    }

    /**
     * Creates a finder of the provided modifiers.
     * If several modifiers share the same label, the last one is used.
     * @param modifiers modifiers to find from
     * @return the finder
     */
    static @NotNull ModifierFinder modifierFinder(@NotNull SequencedCollection<? extends @NotNull Modifier> modifiers) {
        return switch (modifiers.size()) {
            case 0 -> EMPTY;
            case 1 -> modifiers.getFirst();
            default -> modifierFinder((Collection<? extends Modifier>) modifiers);
        };
    }

    /**
     * Creates a finder of the provided modifiers.
     * If several modifiers share the same label, the last one is used.
     * @param modifiers modifiers to find from
     * @return the finder
     */
    static @NotNull ModifierFinder modifierFinder(@NotNull Collection<? extends @NotNull Modifier> modifiers) {
        Map<String, Modifier> modifiersMap = new HashMap<>(modifiers.size());
        for (var modifier : modifiers) modifiersMap.put(modifier.label(), modifier);
        return modifiersMap::get;
    }

    /**
     * Creates a finder of the provided modifiers.
     * If several modifiers share the same label, the last one is used.
     * @param modifiers modifiers to find from
     * @return the finder
     */
    static @NotNull ModifierFinder modifierFinder(@NotNull Iterable<? extends @NotNull Modifier> modifiers) {
        Map<String, Modifier> modifiersMap = new HashMap<>();
        for (var modifier : modifiers) modifiersMap.put(modifier.label(), modifier);
        return modifiersMap::get;
    }

    /**
     * Creates a finder backed by the provided map.
     * The map is not copied, so its later changes are reflected by the finder.
     * @param modifiersMap map of modifiers by their label
     * @return the finder
     */
    static @NotNull ModifierFinder modifierFinder(@NotNull Map<String, Modifier> modifiersMap) {
        return modifiersMap::get;
    }

    /**
     * Composes finders into one, querying them in the provided order.
     * @param modifierFinders finders to compose
     * @return the composed finder
     */
    static @NotNull ModifierFinder composeModifierFinders(@NotNull ModifierFinder @NotNull ... modifierFinders) {
        return composeModifierFinders(Arrays.asList(modifierFinders));
    }

    /**
     * Composes finders into one, querying them in the provided order.
     * @param modifierFinders finders to compose
     * @return the composed finder
     */
    static @NotNull ModifierFinder composeModifierFinders(@NotNull Iterable<? extends @NotNull ModifierFinder> modifierFinders) {
        var iterator = modifierFinders.iterator();
        if (!iterator.hasNext()) return EMPTY;
        ModifierFinder result = iterator.next();
        do {
            result = result.thenModifierFinder(iterator.next());
        } while (iterator.hasNext());
        return result;
    }
}
