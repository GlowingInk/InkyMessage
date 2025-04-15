package ink.glowing.text.modifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@FunctionalInterface
public interface ModifierGetter {
    @Nullable Modifier<?> findModifier(@NotNull String name);

    static @NotNull ModifierGetter modifierGetter(@NotNull Modifier<?> modifier) {
        return (name) -> modifier.name().equals(name) ? modifier : null;
    }

    static @NotNull ModifierGetter modifierGetter(@NotNull Modifier<?> @NotNull ... modifiers) {
        return modifierGetter(Arrays.asList(modifiers));
    }

    static @NotNull ModifierGetter modifierGetter(@NotNull SequencedCollection<@NotNull Modifier<?>> modifiers) {
        return switch (modifiers.size()) {
            case 0 -> (name) -> null;
            case 1 -> modifierGetter(modifiers.getFirst());
            default -> modifierGetter((Collection<? extends Modifier<?>>) modifiers);
        };
    }

    static @NotNull ModifierGetter modifierGetter(@NotNull Collection<? extends @NotNull Modifier<?>> modifiers) {
        Map<String, Modifier<?>> modifiersMap = new HashMap<>(modifiers.size());
        for (var modifier : modifiers) modifiersMap.put(modifier.name(), modifier);
        return modifiersMap::get;
    }

    static @NotNull ModifierGetter modifierGetter(@NotNull Iterable<? extends @NotNull Modifier<?>> modifiers) {
        Map<String, Modifier<?>> modifiersMap = new HashMap<>();
        for (var modifier : modifiers) modifiersMap.put(modifier.name(), modifier);
        return modifiersMap::get;
    }

    static @NotNull ModifierGetter composeModifierGetters(@NotNull ModifierGetter @NotNull ... modifierGetters) {
        return composeModifierGetters(Arrays.asList(modifierGetters));
    }

    static @NotNull ModifierGetter composeModifierGetters(@NotNull Iterable<? extends @NotNull ModifierGetter> modifierGetters) {
        var iterator = modifierGetters.iterator();
        ModifierGetter result = (name) -> null;
        while (iterator.hasNext()) {
            ModifierGetter last = result;
            ModifierGetter next = iterator.next();
            result = (name) -> {
                Modifier modifier = next.findModifier(name);
                return modifier == null ? last.findModifier(name) : modifier;
            };
        }
        return result;
    }
}
