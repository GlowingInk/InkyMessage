package ink.glowing.text.placeholder;

import ink.glowing.text.Context;
import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.modifier.ModifierFinder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

import static net.kyori.adventure.text.Component.text;

record PlaceholderImpl(
        @NotNull String label,
        @NotNull BiFunction<@NotNull String, @NotNull Context, @NotNull Component> resultFunct,
        @NotNull ModifierFinder modifierFinder
) implements Placeholder {
    @Override
    public @NotNull Component retrieve(@NotNull String value, @NotNull Context context) {
        return resultFunct.apply(value, context);
    }

    @Override
    public @Nullable Modifier findLocalModifier(@NotNull String modifierName) {
        return modifierFinder.findModifier(modifierName);
    }

    record Literal(
            @NotNull String label,
            @NotNull BiFunction<@NotNull String, @NotNull Context, @NotNull String> resultFunct
    ) implements Placeholder {
        @Override
        public @NotNull Component retrieve(@NotNull String value, @NotNull Context context) {
            return text(resultFunct.apply(value, context));
        }

        @Override
        public @NotNull String retrievePlain(@NotNull String value, @NotNull Context context) {
            return resultFunct.apply(value, context);
        }
    }
}
