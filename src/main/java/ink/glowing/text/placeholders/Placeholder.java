package ink.glowing.text.placeholders;

import ink.glowing.text.style.modifier.ModifierGetter;
import ink.glowing.text.utils.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Placeholder extends Named, PlaceholderGetter, ModifierGetter {
    @NotNull Component parse(@NotNull String value);

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Component result) {
        return placeholder(name, (v) -> result);
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Component result,
                                            @NotNull ModifierGetter localModifiers) {
        return placeholder(name, (v) -> result, localModifiers);
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Supplier<@NotNull Component> result) {
        return placeholder(name, (v) -> result.get());
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Supplier<@NotNull Component> result,
                                            @NotNull ModifierGetter localModifiers) {
        return placeholder(name, (v) -> result.get(), localModifiers);
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Function<@NotNull String, @NotNull Component> resultFunct) {
        return placeholder(name, resultFunct, (s) -> null);
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Function<@NotNull String, @NotNull Component> resultFunct,
                                            @NotNull ModifierGetter localModifiers) {
        return new PlaceholderImpl(name, resultFunct, localModifiers);
    }
}
