package ink.glowing.text.modifier.standard;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.placeholder.StandardPlaceholders;
import ink.glowing.text.utils.Named;
import ink.glowing.text.utils.Named.NamePattern;
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

public final class StandardModifiers { private StandardModifiers() {}
    private static final Collection<Modifier<?>> STANDARD = Set.of(
            clickModifier(),
            colorModifier(),
            decorModifier(),
            fontModifier(),
            hoverModifier(),
            httpModifier(),
            httpsModifier()
    );
    private static final Map<String, Modifier<?>> STANDARD_MAP = Collections.unmodifiableMap(
            STANDARD.stream().collect(Collectors.toMap(Named::name, identity()))
    );

    /**
     * A list of modifiers that are used by the standard InkyMessage instance.
     * @see StandardModifiers#clickModifier()
     * @see StandardModifiers#colorModifier()
     * @see StandardModifiers#decorModifier()
     * @see StandardModifiers#fontModifier()
     * @see StandardModifiers#hoverModifier()
     * @see StandardModifiers#httpModifier()
     * @see StandardModifiers#httpsModifier()
     */
    public static @NotNull @Unmodifiable Collection<Modifier<?>> standardModifiers() {
        return STANDARD;
    }

    /**
     * A map of modifiers that are used by the standard InkyMessage instance.
     * @see StandardModifiers#standardModifiers()
     */
    public static @NotNull @Unmodifiable Map<String, Modifier<?>> standardModifiersMap() {
        return STANDARD_MAP;
    }

    public static @NotNull Modifier.Plain clickModifier() {
        return ClickModifier.INSTANCE;
    }

    public static @NotNull Modifier.Plain colorModifier() {
        return ColorModifier.INSTANCE;
    }

    public static @NotNull Modifier.Plain decorModifier() {
        return DecorModifier.INSTANCE;
    }

    public static @NotNull Modifier.Plain fontModifier() {
        return FontModifier.INSTANCE;
    }

    public static @NotNull Modifier.Complex hoverModifier() {
        return HoverModifier.INSTANCE;
    }

    public static @NotNull Modifier.Plain httpModifier() {
        return UrlModifier.HTTP;
    }

    public static @NotNull Modifier.Plain httpsModifier() {
        return UrlModifier.HTTPS;
    }

    /**
     * Creates a modifier that allows to open specified URL using provided scheme.
     * @param scheme scheme (prefix) for the URL
     */
    public static @NotNull Modifier.Plain urlModifier(@Pattern("[a-z\\d\\+\\-]+") @NotNull String scheme) {
        return new UrlModifier(scheme);
    }

    /**
     * Used for {@link net.kyori.adventure.text.TranslatableComponent}: adds arguments to the component.
     * Local modifier of {@link StandardPlaceholders#langPlaceholder()}.
     */
    public static @NotNull Modifier.Complex langArgModifier() {
        return LangModifiers.ArgModifier.INSTANCE;
    }

    /**
     * Used for {@link net.kyori.adventure.text.TranslatableComponent}: adds fallback to the component.
     * Local modifier of {@link StandardPlaceholders#langPlaceholder()}.
     */
    public static @NotNull Modifier.Plain langFallbackModifier() {
        return LangModifiers.FallbackModifier.INSTANCE;
    }

    /**
     * Used for {@link net.kyori.adventure.text.SelectorComponent}: specifies separator of the component.
     * Local modifier of {@link StandardPlaceholders#selectorPlaceholder()}.
     */
    public static @NotNull Modifier.Complex selectorSeparatorModifier() {
        return SeparatorModifier.INSTANCE;
    }

    /**
     * An extra modifier that repeats the component.
     */
    public static @NotNull Modifier.Plain repeatModifier() {
        return RepeatModifier.INSTANCE;
    }

    /**
     * Creates a modifier that runs specified action when clicked.
     * @param name name of modifier
     * @param action action to perform
     */
    public static @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                           @NotNull Runnable action) {
        return new CallbackModifier(name, () -> callback(audience -> action.run()));
    }

    /**
     * Creates a modifier that runs specified action when clicked.
     * @param name name of modifier
     * @param action action to perform
     */
    public static @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                           @NotNull Consumer<Audience> action) {
        return new CallbackModifier(name, () -> callback(action::accept));
    }

    /**
     * Creates a modifier that runs specified action when clicked.
     * @param name name of modifier
     * @param action action to perform
     */
    public static @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                           @NotNull ClickCallback<Audience> action) {
        return new CallbackModifier(name, () -> callback(action));
    }

    /**
     * Creates a modifier that runs specified action when clicked.
     * @param name name of modifier
     * @param action action to perform
     */
    public static @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                           @NotNull ClickCallback<Audience> action,
                                                           @NotNull ClickCallback.Options options) {
        return new CallbackModifier(name, () -> callback(action, options));
    }

    /**
     * Creates a modifier that runs specified action when clicked.
     * @param name name of modifier
     * @param action action to perform
     */
    public static @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                           @NotNull ClickCallback<Audience> action,
                                                           @NotNull Consumer<ClickCallback.Options. @NotNull Builder> optionsBuilder) {
        return new CallbackModifier(name, () -> callback(action, optionsBuilder));
    }
}
