package ink.glowing.text.placeholder;

import ink.glowing.text.Ink;
import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.modifier.ModifierFinder;
import ink.glowing.text.utils.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

// TODO VirtualComponent?
public interface Placeholder extends Ink, Named, PlaceholderFinder {
    @NotNull Component parse(@NotNull String value);

    @Override
    default @Nullable Placeholder findPlaceholder(@NotNull String name) {
        return name.equals(name()) ? this : null;
    }

    default @Nullable Modifier<?> findLocalModifier(@NotNull String name) {
        return null;
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Component result) {
        return placeholder(name, (v) -> result);
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Component result,
                                            @NotNull ModifierFinder localModifiers) {
        return placeholder(name, (v) -> result, localModifiers);
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Supplier<@NotNull Component> result) {
        return placeholder(name, (v) -> result.get());
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Supplier<@NotNull Component> result,
                                            @NotNull ModifierFinder localModifiers) {
        return placeholder(name, (v) -> result.get(), localModifiers);
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Function<@NotNull String, @NotNull Component> resultFunct) {
        return placeholder(name, resultFunct, (s) -> null);
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Function<@NotNull String, @NotNull Component> resultFunct,
                                            @NotNull ModifierFinder localModifiers) {
        return new PlaceholderImpl(name, resultFunct, localModifiers);
    }
}
