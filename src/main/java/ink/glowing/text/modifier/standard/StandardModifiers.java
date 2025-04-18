package ink.glowing.text.modifier.standard;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.utils.Named.NamePattern;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.function.Consumer;

public final class StandardModifiers { private StandardModifiers() {}
    private static final Set<Modifier<?>> STANDARD = Set.of(
            clickModifier(),
            colorModifier(),
            decorModifier(),
            fontModifier(),
            hoverModifier(),
            httpModifier(),
            httpsModifier()
    );

    /**
     * A list of modifiers that are used by the standard resolver
     * @see StandardModifiers#clickModifier()
     * @see StandardModifiers#colorModifier()
     * @see StandardModifiers#decorModifier()
     * @see StandardModifiers#fontModifier()
     * @see StandardModifiers#hoverModifier()
     * @see StandardModifiers#httpModifier()
     * @see StandardModifiers#httpsModifier()
     */
    public static @NotNull @Unmodifiable Set<Modifier<?>> standardModifiers() {
        return STANDARD;
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

    public static @NotNull Modifier.Plain urlModifier(@Pattern("[a-z\\d\\+\\-]+") @NotNull String scheme) {
        return new UrlModifier(scheme);
    }

    /**
     * Used for {@link net.kyori.adventure.text.TranslatableComponent}
     */
    public static @NotNull Modifier.Complex langArgModifier() {
        return LangModifiers.ArgModifier.INSTANCE;
    }

    /**
     * Used for {@link net.kyori.adventure.text.TranslatableComponent}
     */
    public static @NotNull Modifier.Plain langFallbackModifier() {
        return LangModifiers.FallbackModifier.INSTANCE;
    }

    /**
     * Used for {@link net.kyori.adventure.text.SelectorComponent}
     */
    public static @NotNull Modifier.Complex selectorSeparatorModifier() {
        return SeparatorModifier.INSTANCE;
    }

    public static @NotNull Modifier.Plain repeatModifier() {
        return RepeatModifier.INSTANCE;
    }

    public static @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                           @NotNull Runnable action) {
        return new CallbackModifier(name, () -> ClickEvent.callback(audience -> action.run()));
    }

    public static @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                           @NotNull Consumer<Audience> action) {
        return new CallbackModifier(name, () -> ClickEvent.callback(action::accept));
    }

    public static @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                           @NotNull ClickCallback<Audience> action) {
        return new CallbackModifier(name, () -> ClickEvent.callback(action));
    }

    public static @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                           @NotNull ClickCallback<Audience> action,
                                                           @NotNull ClickCallback.Options options) {
        return new CallbackModifier(name, () -> ClickEvent.callback(action, options));
    }

    public static @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                           @NotNull ClickCallback<Audience> action,
                                                           @NotNull Consumer<ClickCallback.Options. @NotNull Builder> optionsBuilder) {
        return new CallbackModifier(name, () -> ClickEvent.callback(action, optionsBuilder));
    }
}
