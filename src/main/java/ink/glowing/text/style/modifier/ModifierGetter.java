package ink.glowing.text.style.modifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface ModifierGetter {
    @Nullable StyleModifier<?> findModifier(@NotNull String name);

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

    static @NotNull ModifierGetter modifierGetter(@NotNull Iterable<? extends StyleModifier<?>> modifiers) {
        Map<String, StyleModifier<?>> modifiersMap = new HashMap<>();
        for (var modifier : modifiers) modifiersMap.put(modifier.name(), modifier);
        return modifiersMap::get;
    }
}
