package ink.glowing.text.placeholders;

import ink.glowing.text.style.tag.TagGetter;
import ink.glowing.text.utils.Named;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Placeholder extends Named, PlaceholderGetter, TagGetter permits PlaceholderImpl {
    @NotNull Component parse(@NotNull String value);

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Component result) {
        return placeholder(name, (v) -> result);
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Supplier<@NotNull Component> result) {
        return placeholder(name, (v) -> result.get());
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Function<@NotNull String, @NotNull Component> resultFunct) {
        return placeholder(name, resultFunct, (s) -> null);
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Component result,
                                            @NotNull TagGetter localTags) {
        return placeholder(name, (v) -> result, localTags);
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Supplier<@NotNull Component> result,
                                            @NotNull TagGetter localTags) {
        return placeholder(name, (v) -> result.get(), localTags);
    }

    static @NotNull Placeholder placeholder(@NotNull String name,
                                            @NotNull Function<@NotNull String, @NotNull Component> resultFunct,
                                            @NotNull TagGetter localTags) {
        return new PlaceholderImpl(name, resultFunct, localTags);
    }
}
