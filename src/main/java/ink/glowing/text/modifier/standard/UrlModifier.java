package ink.glowing.text.modifier.standard;

import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

public final class UrlModifier implements Modifier.Plain {
    private static final Modifier.Plain HTTP_MODIFIER = urlModifier("http");
    private static final Modifier.Plain HTTPS_MODIFIER = urlModifier("https");

    private final @Subst("ms-web+lmao3") String scheme;
    
    private UrlModifier(@NotNull String scheme) {
        this.scheme = scheme;
    }

    public static @NotNull Modifier.Plain httpModifier() {
        return HTTP_MODIFIER;
    }

    public static @NotNull Modifier.Plain httpsModifier() {
        return HTTPS_MODIFIER;
    }

    public static @NotNull Modifier.Plain urlModifier(@Pattern("[a-z\\d\\+\\-]+") @NotNull String scheme) {
        return new UrlModifier(scheme);
    }
    
    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
        if (!param.startsWith("//") || param.length() < 3) return text;
        return text.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, scheme + ":" + param));
    }

    @Override
    public @NotNull @NamePattern String name() {
        return scheme;
    }
}
