package ink.glowing.text.placeholder;

import ink.glowing.text.Context;
import ink.glowing.text.Ink;
import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.modifier.ModifierFinder;
import ink.glowing.text.utils.Labeled;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

// TODO VirtualComponent?
public interface Placeholder extends Ink, Labeled, PlaceholderFinder {
    @NotNull Component retrieve(@NotNull String value, @NotNull Context context);
    
    default @NotNull String retrievePlain(@NotNull String value, @NotNull Context context) {
        StringBuilder builder = new StringBuilder();
        ComponentFlattener.basic().flatten(retrieve(value, context), builder::append);
        return builder.toString();
    }

    @Override
    default @Nullable Placeholder findPlaceholder(@NotNull String label) {
        return label.equals(label()) ? this : null;
    }

    default @Nullable Modifier findLocalModifier(@NotNull String label) {
        return null;
    }

    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull Component result) {
        return placeholder(label, (v, c) -> result, ModifierFinder.EMPTY);
    }

    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull Component result, @NotNull ModifierFinder localModifiers) {
        return placeholder(label, (v, c) -> result, localModifiers);
    }

    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull Supplier<@NotNull Component> result) {
        return placeholder(label, (v, c) -> result.get(), ModifierFinder.EMPTY);
    }

    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull Supplier<@NotNull Component> result, @NotNull ModifierFinder localModifiers) {
        return placeholder(label, (v, c) -> result.get(), localModifiers);
    }

    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull Function<@NotNull String, @NotNull Component> resultFunct) {
        return placeholder(label, (v, c) -> resultFunct.apply(v), ModifierFinder.EMPTY);
    }

    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull Function<@NotNull String, @NotNull Component> resultFunct, @NotNull ModifierFinder localModifiers) {
        return placeholder(label, (v, c) -> resultFunct.apply(v), localModifiers);
    }

    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull BiFunction<@NotNull String, @NotNull Context, @NotNull Component> resultFunct) {
        return placeholder(label, resultFunct, ModifierFinder.EMPTY);
    }

    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull BiFunction<@NotNull String, @NotNull Context, @NotNull Component> resultFunct, @NotNull ModifierFinder localModifiers) {
        return new PlaceholderImpl(label, resultFunct, localModifiers);
    }

    static @NotNull Placeholder literalPlaceholder(@NotNull String label, @NotNull String result) {
        return literalPlaceholder(label, (v, c) -> result);
    }

    static @NotNull Placeholder literalPlaceholder(@NotNull String label, @NotNull Supplier<@NotNull String> result) {
        return literalPlaceholder(label, (v, c) -> result.get());
    }

    static @NotNull Placeholder literalPlaceholder(@NotNull String label, @NotNull Function<@NotNull String, @NotNull String> result) {
        return literalPlaceholder(label, (v, c) -> result.apply(v));
    }

    static @NotNull Placeholder literalPlaceholder(@NotNull String label, @NotNull BiFunction<@NotNull String, @NotNull Context, @NotNull String> result) {
        return new PlaceholderImpl.Literal(label, result);
    }

    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull String result) {
        return parsingPlaceholder(label, () -> result, ModifierFinder.EMPTY);
    }

    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull String result, @NotNull ModifierFinder localModifiers) {
        return parsingPlaceholder(label, () -> result, localModifiers);
    }

    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull Supplier<@NotNull String> result) {
        return parsingPlaceholder(label, result, ModifierFinder.EMPTY);
    }

    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull Supplier<@NotNull String> result, @NotNull ModifierFinder localModifiers) {
        return parsingPlaceholder(label, (v, c) -> result.get(), localModifiers);
    }

    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull Function<@NotNull String, @NotNull String> result) {
        return parsingPlaceholder(label, (v, c) -> result.apply(v), ModifierFinder.EMPTY);
    }

    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull Function<@NotNull String, @NotNull String> result, @NotNull ModifierFinder localModifiers) {
        return parsingPlaceholder(label, (v, c) -> result.apply(v), localModifiers);
    }

    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull BiFunction<@NotNull String, @NotNull Context, @NotNull String> result) {
        return parsingPlaceholder(label, result, ModifierFinder.EMPTY);
    }

    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull BiFunction<@NotNull String, @NotNull Context, @NotNull String> result, @NotNull ModifierFinder localModifiers) {
        return new PlaceholderImpl(label, (v, c) -> c.deserialize(result.apply(v, c)), localModifiers);
    }
}
