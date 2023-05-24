package ink.glowing.text.placeholders;

import ink.glowing.text.style.modifier.ModifierGetter;
import ink.glowing.text.style.modifier.StyleModifier;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

record PlaceholderImpl(
        @NotNull String name,
        @NotNull Function<@NotNull String, @NotNull Component> resultFunct,
        @NotNull ModifierGetter modifierGetter
) implements Placeholder {
    @Override
    public @NotNull Component parse(@NotNull String value) {
        return resultFunct.apply(value);
    }

    @Override
    public @Nullable StyleModifier<?> findModifier(@NotNull String modifierName) {
        return modifierGetter.findModifier(modifierName);
    }
}
