package ink.glowing.text.modifier.standard;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.placeholder.StandardPlaceholders;
import ink.glowing.text.utils.Labeled;
import ink.glowing.text.utils.Labeled.LabelPattern;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.ClickCallback;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static net.kyori.adventure.text.event.ClickEvent.callback;

/**
 * Modifiers provided by InkyMessage.
 */
public final class StandardModifiers {
    private StandardModifiers() { }

    private static final Collection<Modifier> STANDARD = Set.of(
            clickModifier(),
            colorModifier(),
            decorModifier(),
            fontModifier(),
            hoverModifier(),
            httpModifier(),
            httpsModifier()
    );
    private static final Map<String, Modifier> STANDARD_MAP = Collections.unmodifiableMap(
            STANDARD.stream().collect(Collectors.toMap(Labeled::label, identity()))
    );

    /**
     * A list of modifiers that are used by the standard InkyMessage instance.
     * @return standard modifiers
     * @see StandardModifiers#clickModifier()
     * @see StandardModifiers#colorModifier()
     * @see StandardModifiers#decorModifier()
     * @see StandardModifiers#fontModifier()
     * @see StandardModifiers#hoverModifier()
     * @see StandardModifiers#httpModifier()
     * @see StandardModifiers#httpsModifier()
     */
    public static @NotNull @Unmodifiable Collection<Modifier> standardModifiers() {
        return STANDARD;
    }

    /**
     * A map of modifiers that are used by the standard InkyMessage instance.
     * @return map of standard modifiers by their labels
     * @see StandardModifiers#standardModifiers()
     */
    public static @NotNull @Unmodifiable Map<String, Modifier> standardModifiersMap() {
        return STANDARD_MAP;
    }

    /**
     * Modifier that sets click event or insertion of the text, e.g. {@code (click:run /help)}.
     * Parameter is the action: {@code url}, {@code run}, {@code suggest}, {@code copy}, {@code page}, {@code file} or {@code insert}.
     * @return the modifier
     */
    public static @NotNull Modifier clickModifier() {
        return ClickModifier.INSTANCE;
    }

    /**
     * Modifier that sets color of the text, e.g. {@code (color:red)}.
     * Parameter is a color name, hex color, several of them joined with {@code -} for a gradient, {@code spectrum} or {@code random}.
     * Value {@code pastel} makes colors paler.
     * @return the modifier
     */
    public static @NotNull Modifier colorModifier() {
        return ColorModifier.INSTANCE;
    }

    /**
     * Modifier that sets decoration of the text, e.g. {@code (decor:bold)}.
     * Value {@code false} or {@code unset} removes or unsets the decoration.
     * @return the modifier
     */
    public static @NotNull Modifier decorModifier() {
        return DecorModifier.INSTANCE;
    }

    /**
     * Modifier that sets font of the text, e.g. {@code (font:minecraft:uniform)}.
     * @return the modifier
     */
    public static @NotNull Modifier fontModifier() {
        return FontModifier.INSTANCE;
    }

    /**
     * Modifier that sets text shown on hover, e.g. {@code (hover &cWarning!)}.
     * @return the modifier
     */
    public static @NotNull Modifier hoverModifier() {
        return HoverModifier.INSTANCE;
    }

    /**
     * Modifier that opens HTTP URL on click, e.g. {@code (http://example.com)}.
     * @return the modifier
     * @see StandardModifiers#urlModifier(String)
     */
    public static @NotNull Modifier httpModifier() {
        return UrlModifier.HTTP;
    }

    /**
     * Modifier that opens HTTPS URL on click, e.g. {@code (https://example.com)}.
     * @return the modifier
     * @see StandardModifiers#urlModifier(String)
     */
    public static @NotNull Modifier httpsModifier() {
        return UrlModifier.HTTPS;
    }

    /**
     * Creates a modifier that allows to open specified URL using provided scheme.
     * @param scheme scheme (prefix) for the URL
     * @return a new modifier
     */
    public static @NotNull Modifier urlModifier(@Pattern("[a-z\\d+\\-]+") @NotNull String scheme) {
        return new UrlModifier(scheme);
    }

    /**
     * Used for {@link net.kyori.adventure.text.TranslatableComponent}: adds an argument to the component.
     * Parameter optionally specifies the one-based position of argument.
     * Local modifier of {@link StandardPlaceholders#langPlaceholder()}.
     * @return the modifier
     */
    public static @NotNull Modifier langArgModifier() {
        return LangModifiers.ArgModifier.INSTANCE;
    }

    /**
     * Used for {@link net.kyori.adventure.text.TranslatableComponent}: replaces all arguments of the component.
     * Local modifier of {@link StandardPlaceholders#langPlaceholder()}.
     * @return the modifier
     */
    public static @NotNull Modifier langArgsModifier() {
        return LangModifiers.ArgsModifier.INSTANCE;
    }

    /**
     * Used for {@link net.kyori.adventure.text.TranslatableComponent}: adds fallback to the component.
     * Local modifier of {@link StandardPlaceholders#langPlaceholder()}.
     * @return the modifier
     */
    public static @NotNull Modifier langFallbackModifier() {
        return LangModifiers.FallbackModifier.INSTANCE;
    }

    /**
     * Used for {@link net.kyori.adventure.text.SelectorComponent}: specifies separator of the component.
     * Local modifier of {@link StandardPlaceholders#selectorPlaceholder()}.
     * @return the modifier
     */
    public static @NotNull Modifier selectorSeparatorModifier() {
        return SeparatorModifier.INSTANCE;
    }

    /**
     * An extra modifier that repeats the component.
     * Parameter is the number of additional repetitions.
     * @return the modifier
     */
    public static @NotNull Modifier repeatModifier() {
        return RepeatModifier.INSTANCE;
    }

    /**
     * Creates a modifier that runs specified action when clicked.
     * @param label label of modifier
     * @param action action to perform
     * @return a new modifier
     */
    public static @NotNull Modifier callbackModifier(@NotNull @LabelPattern String label,
                                                     @NotNull Runnable action) {
        return new CallbackModifier(label, () -> callback(audience -> action.run()));
    }

    /**
     * Creates a modifier that runs specified action when clicked.
     * @param label label of modifier
     * @param action action to perform
     * @return a new modifier
     */
    public static @NotNull Modifier callbackModifier(@NotNull @LabelPattern String label,
                                                     @NotNull Consumer<Audience> action) {
        return new CallbackModifier(label, () -> callback(action::accept));
    }

    /**
     * Creates a modifier that runs specified action when clicked.
     * @param label label of modifier
     * @param action action to perform
     * @return a new modifier
     */
    public static @NotNull Modifier callbackModifier(@NotNull @LabelPattern String label,
                                                     @NotNull ClickCallback<Audience> action) {
        return new CallbackModifier(label, () -> callback(action));
    }

    /**
     * Creates a modifier that runs specified action when clicked.
     * @param label label of modifier
     * @param action action to perform
     * @param options callback options
     * @return a new modifier
     */
    public static @NotNull Modifier callbackModifier(@NotNull @LabelPattern String label,
                                                     @NotNull ClickCallback<Audience> action,
                                                     @NotNull ClickCallback.Options options) {
        return new CallbackModifier(label, () -> callback(action, options));
    }

    /**
     * Creates a modifier that runs specified action when clicked.
     * @param label label of modifier
     * @param action action to perform
     * @param optionsBuilder configurator of callback options
     * @return a new modifier
     */
    public static @NotNull Modifier callbackModifier(@NotNull @LabelPattern String label,
                                                     @NotNull ClickCallback<Audience> action,
                                                     @NotNull Consumer<ClickCallback.Options. @NotNull Builder> optionsBuilder) {
        return new CallbackModifier(label, () -> callback(action, optionsBuilder));
    }
}
