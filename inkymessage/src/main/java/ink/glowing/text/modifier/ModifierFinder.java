package ink.glowing.text.modifier;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@FunctionalInterface
public interface ModifierFinder {
    ModifierFinder EMPTY = (label) -> null;

    @Contract(pure = true)
    @Nullable Modifier findModifier(@NotNull String label);

    default @NotNull ModifierFinder thenModifierFinder(@NotNull ModifierFinder modifierFinder) {
        return (label) -> {
            var modifier = findModifier(label);
            return modifier != null ? modifier : modifierFinder.findModifier(label);
        };
    }

    static @NotNull ModifierFinder modifierFinder(@NotNull Modifier @NotNull ... modifiers) {
        return modifierFinder(Arrays.asList(modifiers));
    }

    static @NotNull ModifierFinder modifierFinder(@NotNull SequencedCollection<? extends @NotNull Modifier> modifiers) {
        return switch (modifiers.size()) {
            case 0 -> EMPTY;
            case 1 -> modifiers.getFirst();
            default -> modifierFinder((Collection<? extends Modifier>) modifiers);
        };
    }

    static @NotNull ModifierFinder modifierFinder(@NotNull Collection<? extends @NotNull Modifier> modifiers) {
        Map<String, Modifier> modifiersMap = new HashMap<>(modifiers.size());
        for (var modifier : modifiers) modifiersMap.put(modifier.label(), modifier);
        return modifiersMap::get;
    }

    static @NotNull ModifierFinder modifierFinder(@NotNull Iterable<? extends @NotNull Modifier> modifiers) {
        Map<String, Modifier> modifiersMap = new HashMap<>();
        for (var modifier : modifiers) modifiersMap.put(modifier.label(), modifier);
        return modifiersMap::get;
    }

    static @NotNull ModifierFinder modifierFinder(@NotNull Map<String, Modifier> modifiersMap) {
        return modifiersMap::get;
    }

    static @NotNull ModifierFinder composeModifierFinders(@NotNull ModifierFinder @NotNull ... modifierFinders) {
        return composeModifierFinders(Arrays.asList(modifierFinders));
    }

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
