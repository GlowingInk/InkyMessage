package ink.glowing.text.modifier;

import ink.glowing.text.Ink;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@FunctionalInterface
public interface ModifierFinder extends Ink {
    @Contract(pure = true)
    @Nullable Modifier<?> findModifier(@NotNull String name);

    default @NotNull ModifierFinder thenModifierFinder(@NotNull ModifierFinder modifierFinder) {
        return (name) -> {
            var modifier = findModifier(name);
            return modifier != null ? modifier : modifierFinder.findModifier(name);
        };
    }

    static @NotNull ModifierFinder modifierFinder(@NotNull Modifier<?> @NotNull ... modifiers) {
        return modifierFinder(Arrays.asList(modifiers));
    }

    static @NotNull ModifierFinder modifierFinder(@NotNull SequencedCollection<@NotNull Modifier<?>> modifiers) {
        return switch (modifiers.size()) {
            case 0 -> (name) -> null;
            case 1 -> modifiers.getFirst();
            default -> modifierFinder((Collection<? extends Modifier<?>>) modifiers);
        };
    }

    static @NotNull ModifierFinder modifierFinder(@NotNull Collection<? extends @NotNull Modifier<?>> modifiers) {
        Map<String, Modifier<?>> modifiersMap = new HashMap<>(modifiers.size());
        for (var modifier : modifiers) modifiersMap.put(modifier.name(), modifier);
        return modifiersMap::get;
    }

    static @NotNull ModifierFinder modifierFinder(@NotNull Iterable<? extends @NotNull Modifier<?>> modifiers) {
        Map<String, Modifier<?>> modifiersMap = new HashMap<>();
        for (var modifier : modifiers) modifiersMap.put(modifier.name(), modifier);
        return modifiersMap::get;
    }

    static @NotNull ModifierFinder modifierFinder(@NotNull Map<String, Modifier<?>> modifiersMap) {
        return modifiersMap::get;
    }

    static @NotNull ModifierFinder composeModifierFinders(@NotNull ModifierFinder @NotNull ... modifierFinders) {
        return composeModifierFinders(Arrays.asList(modifierFinders));
    }

    static @NotNull ModifierFinder composeModifierFinders(@NotNull Iterable<? extends @NotNull ModifierFinder> modifierFinders) {
        var iterator = modifierFinders.iterator();
        if (!iterator.hasNext()) return (symbol) -> null;
        ModifierFinder result = iterator.next();
        do {
            result = result.thenModifierFinder(iterator.next());
        } while (iterator.hasNext());
        return result;
    }
}
