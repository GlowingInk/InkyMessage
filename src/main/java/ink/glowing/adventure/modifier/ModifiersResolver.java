package ink.glowing.adventure.modifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifiersResolver {
    private final Map<String, Modifier> modifiers;

    public ModifiersResolver(@NotNull Modifier @NotNull ... modifiers) {
        this(List.of(modifiers));
    }

    public ModifiersResolver(@NotNull Iterable<Modifier> modifiers) {
        Map<String, Modifier> mods = new HashMap<>();
        for (var mod : modifiers) {
            mods.put(mod.namespace(), mod);
        }
        this.modifiers = Collections.unmodifiableMap(mods);
    }

    public @Nullable Modifier getModifier(@NotNull String name) {
        return modifiers.get(name);
    }
}
