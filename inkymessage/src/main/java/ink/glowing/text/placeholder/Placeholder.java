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
/**
 * Ink that inserts a component in place of {@code &{label:value}}.
 * Placeholders may also provide local modifiers, applicable only to their result.
 */
public non-sealed interface Placeholder extends Ink, Labeled, PlaceholderFinder {
    /**
     * Retrieves the placeholder's result.
     * @param value placeholder parameter
     * @param context current context
     * @return resulting component
     */
    @NotNull Component retrieve(@NotNull String value, @NotNull Context context);
    
    /**
     * Retrieves the placeholder's result as plain text.
     * Used when the placeholder is inside a plain modifier argument.
     * @param value placeholder parameter
     * @param context current context
     * @return resulting plain text
     */
    default @NotNull String retrievePlain(@NotNull String value, @NotNull Context context) {
        StringBuilder builder = new StringBuilder();
        ComponentFlattener.basic().flatten(retrieve(value, context), builder::append);
        return builder.toString();
    }

    @Override
    default @Nullable Placeholder findPlaceholder(@NotNull String label) {
        return label.equals(label()) ? this : null;
    }

    /**
     * Finds modifier applicable only to this placeholder's result.
     * @param label modifier label
     * @return found modifier, or {@code null} if none
     */
    default @Nullable Modifier findLocalModifier(@NotNull String label) {
        return null;
    }

    /**
     * Creates a placeholder with constant result.
     * @param label placeholder label
     * @param result placeholder result
     * @return the placeholder
     */
    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull Component result) {
        return placeholder(label, (_, _) -> result, ModifierFinder.EMPTY);
    }

    /**
     * Creates a placeholder with constant result.
     * @param label placeholder label
     * @param result placeholder result
     * @param localModifiers modifiers applicable only to the placeholder result
     * @return the placeholder
     */
    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull Component result, @NotNull ModifierFinder localModifiers) {
        return placeholder(label, (_, _) -> result, localModifiers);
    }

    /**
     * Creates a placeholder with supplied result.
     * @param label placeholder label
     * @param result supplier of placeholder result
     * @return the placeholder
     */
    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull Supplier<@NotNull Component> result) {
        return placeholder(label, (_, _) -> result.get(), ModifierFinder.EMPTY);
    }

    /**
     * Creates a placeholder with supplied result.
     * @param label placeholder label
     * @param result supplier of placeholder result
     * @param localModifiers modifiers applicable only to the placeholder result
     * @return the placeholder
     */
    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull Supplier<@NotNull Component> result, @NotNull ModifierFinder localModifiers) {
        return placeholder(label, (_, _) -> result.get(), localModifiers);
    }

    /**
     * Creates a placeholder with result depending on its parameter.
     * @param label placeholder label
     * @param resultFunct function converting placeholder parameter into result
     * @return the placeholder
     */
    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull Function<@NotNull String, @NotNull Component> resultFunct) {
        return placeholder(label, (v, _) -> resultFunct.apply(v), ModifierFinder.EMPTY);
    }

    /**
     * Creates a placeholder with result depending on its parameter.
     * @param label placeholder label
     * @param resultFunct function converting placeholder parameter into result
     * @param localModifiers modifiers applicable only to the placeholder result
     * @return the placeholder
     */
    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull Function<@NotNull String, @NotNull Component> resultFunct, @NotNull ModifierFinder localModifiers) {
        return placeholder(label, (v, _) -> resultFunct.apply(v), localModifiers);
    }

    /**
     * Creates a placeholder with result depending on its parameter and context.
     * @param label placeholder label
     * @param resultFunct function converting placeholder parameter and context into result
     * @return the placeholder
     */
    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull BiFunction<@NotNull String, @NotNull Context, @NotNull Component> resultFunct) {
        return placeholder(label, resultFunct, ModifierFinder.EMPTY);
    }

    /**
     * Creates a placeholder with result depending on its parameter and context.
     * @param label placeholder label
     * @param resultFunct function converting placeholder parameter and context into result
     * @param localModifiers modifiers applicable only to the placeholder result
     * @return the placeholder
     */
    static @NotNull Placeholder placeholder(@NotNull String label, @NotNull BiFunction<@NotNull String, @NotNull Context, @NotNull Component> resultFunct, @NotNull ModifierFinder localModifiers) {
        return new PlaceholderImpl(label, resultFunct, localModifiers);
    }

    /**
     * Creates a literal placeholder with constant result.
     * The result is inserted as plain text, without parsing.
     * @param label placeholder label
     * @param result placeholder result
     * @return the placeholder
     */
    static @NotNull Placeholder literalPlaceholder(@NotNull String label, @NotNull String result) {
        return literalPlaceholder(label, (_, _) -> result);
    }

    /**
     * Creates a literal placeholder with supplied result.
     * The result is inserted as plain text, without parsing.
     * @param label placeholder label
     * @param result supplier of placeholder result
     * @return the placeholder
     */
    static @NotNull Placeholder literalPlaceholder(@NotNull String label, @NotNull Supplier<@NotNull String> result) {
        return literalPlaceholder(label, (_, _) -> result.get());
    }

    /**
     * Creates a literal placeholder with result depending on its parameter.
     * The result is inserted as plain text, without parsing.
     * @param label placeholder label
     * @param result function converting placeholder parameter into result
     * @return the placeholder
     */
    static @NotNull Placeholder literalPlaceholder(@NotNull String label, @NotNull Function<@NotNull String, @NotNull String> result) {
        return literalPlaceholder(label, (v, _) -> result.apply(v));
    }

    /**
     * Creates a literal placeholder with result depending on its parameter and context.
     * The result is inserted as plain text, without parsing.
     * @param label placeholder label
     * @param result function converting placeholder parameter and context into result
     * @return the placeholder
     */
    static @NotNull Placeholder literalPlaceholder(@NotNull String label, @NotNull BiFunction<@NotNull String, @NotNull Context, @NotNull String> result) {
        return new PlaceholderImpl.Literal(label, result);
    }

    /**
     * Creates a parsing placeholder with constant result.
     * The result is parsed using the current context.
     * @param label placeholder label
     * @param result placeholder result to parse
     * @return the placeholder
     */
    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull String result) {
        return parsingPlaceholder(label, () -> result, ModifierFinder.EMPTY);
    }

    /**
     * Creates a parsing placeholder with constant result.
     * The result is parsed using the current context.
     * @param label placeholder label
     * @param result placeholder result to parse
     * @param localModifiers modifiers applicable only to the placeholder result
     * @return the placeholder
     */
    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull String result, @NotNull ModifierFinder localModifiers) {
        return parsingPlaceholder(label, () -> result, localModifiers);
    }

    /**
     * Creates a parsing placeholder with supplied result.
     * The result is parsed using the current context.
     * @param label placeholder label
     * @param result supplier of placeholder result to parse
     * @return the placeholder
     */
    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull Supplier<@NotNull String> result) {
        return parsingPlaceholder(label, result, ModifierFinder.EMPTY);
    }

    /**
     * Creates a parsing placeholder with supplied result.
     * The result is parsed using the current context.
     * @param label placeholder label
     * @param result supplier of placeholder result to parse
     * @param localModifiers modifiers applicable only to the placeholder result
     * @return the placeholder
     */
    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull Supplier<@NotNull String> result, @NotNull ModifierFinder localModifiers) {
        return parsingPlaceholder(label, (_, _) -> result.get(), localModifiers);
    }

    /**
     * Creates a parsing placeholder with result depending on its parameter.
     * The result is parsed using the current context.
     * @param label placeholder label
     * @param result function converting placeholder parameter into result to parse
     * @return the placeholder
     */
    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull Function<@NotNull String, @NotNull String> result) {
        return parsingPlaceholder(label, (v, _) -> result.apply(v), ModifierFinder.EMPTY);
    }

    /**
     * Creates a parsing placeholder with result depending on its parameter.
     * The result is parsed using the current context.
     * @param label placeholder label
     * @param result function converting placeholder parameter into result to parse
     * @param localModifiers modifiers applicable only to the placeholder result
     * @return the placeholder
     */
    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull Function<@NotNull String, @NotNull String> result, @NotNull ModifierFinder localModifiers) {
        return parsingPlaceholder(label, (v, _) -> result.apply(v), localModifiers);
    }

    /**
     * Creates a parsing placeholder with result depending on its parameter and context.
     * The result is parsed using the current context.
     * @param label placeholder label
     * @param result function converting placeholder parameter and context into result to parse
     * @return the placeholder
     */
    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull BiFunction<@NotNull String, @NotNull Context, @NotNull String> result) {
        return parsingPlaceholder(label, result, ModifierFinder.EMPTY);
    }

    /**
     * Creates a parsing placeholder with result depending on its parameter and context.
     * The result is parsed using the current context.
     * @param label placeholder label
     * @param result function converting placeholder parameter and context into result to parse
     * @param localModifiers modifiers applicable only to the placeholder result
     * @return the placeholder
     */
    static @NotNull Placeholder parsingPlaceholder(@NotNull String label, @NotNull BiFunction<@NotNull String, @NotNull Context, @NotNull String> result, @NotNull ModifierFinder localModifiers) {
        return new PlaceholderImpl(label, (v, c) -> c.deserialize(result.apply(v, c)), localModifiers);
    }
}
