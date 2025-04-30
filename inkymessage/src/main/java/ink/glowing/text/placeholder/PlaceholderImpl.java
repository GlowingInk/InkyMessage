package ink.glowing.text.placeholder;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.modifier.ModifierFinder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

record PlaceholderImpl(
        @NotNull String name,
        @NotNull Function<@NotNull String, @NotNull Component> resultFunct,
        @NotNull ModifierFinder modifierFinder
) implements Placeholder {
    @Override
    public @NotNull Component parse(@NotNull String value) {
        return resultFunct.apply(value);
    }

    @Override
    public @Nullable Modifier findLocalModifier(@NotNull String modifierName) {
        return modifierFinder.findModifier(modifierName);
    }
}
