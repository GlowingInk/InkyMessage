package ink.glowing.text.style.modifier;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface ModifierGetter {
    @Nullable StyleModifier<?> findModifier(@NotNull String name);

    @Contract(pure = true)
    default @NotNull ModifierGetter composeModifier(@NotNull ModifierGetter other) {
        return (name) -> {
            var modifier = ModifierGetter.this.findModifier(name);
            return modifier == null ? other.findModifier(name) : modifier;
        };
    }

    @Contract(pure = true)
    default @NotNull ModifierGetter composeModifier(@NotNull ModifierGetter... others) {
        return composeModifier(Arrays.asList(others));
    }

    @Contract(pure = true)
    default @NotNull ModifierGetter composeModifier(@NotNull Iterable<ModifierGetter> others) {
        ModifierGetter result = this;
        for (var other : others) result = result.composeModifier(other);
        return result;
    }

    static @NotNull ModifierGetter modifierGetter(@NotNull StyleModifier<?> modifier) {
        return (name) -> modifier.name().equals(name) ? modifier : null;
    }

    static @NotNull ModifierGetter modifierGetter(@NotNull StyleModifier<?> @NotNull ... modifiers) {
        return switch (modifiers.length) {
            case 0 -> (name) -> null;
            case 1 -> modifierGetter(modifiers[0]);
            default -> modifierGetter(Arrays.asList(modifiers));
        };
    }

    static @NotNull ModifierGetter modifierGetter(@NotNull Iterable<StyleModifier<?>> modifiers) {
        Map<String, StyleModifier<?>> modifiersMap = new HashMap<>();
        for (var modifier : modifiers) modifiersMap.put(modifier.name(), modifier);
        return modifiersMap::get;
    }
}
