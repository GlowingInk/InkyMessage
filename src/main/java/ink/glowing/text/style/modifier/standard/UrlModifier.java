package ink.glowing.text.style.modifier.standard;

import ink.glowing.text.style.modifier.StyleModifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

public class UrlModifier implements StyleModifier.Plain {
    private static final StyleModifier.Plain HTTP_MODIFIER = urlSchemeModifier("http");
    private static final StyleModifier.Plain HTTPS_MODIFIER = urlSchemeModifier("https");

    private final String scheme;
    
    private UrlModifier(@NotNull String scheme) {
        this.scheme = scheme;
    }

    public static @NotNull StyleModifier.Plain httpModifier() {
        return HTTP_MODIFIER;
    }

    public static @NotNull StyleModifier.Plain httpsModifier() {
        return HTTPS_MODIFIER;
    }

    public static @NotNull StyleModifier.Plain urlSchemeModifier(@Pattern(NAME_PATTERN) @NotNull String scheme) {
        return new UrlModifier(scheme);
    }
    
    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
        if (!param.startsWith("//") || param.length() < 3) return text;
        return text.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, scheme + ":" + param));
    }

    @Override
    public @NotNull String name() {
        return scheme;
    }
}
