package ink.glowing.text.modifier.standard;

import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

final class UrlModifier implements Modifier.Plain {
    static final Modifier HTTP = new UrlModifier("http");
    static final Modifier HTTPS = new UrlModifier("https");

    private final @Subst("ms-web+lmao3") String scheme;
    
    UrlModifier(@NotNull String scheme) {
        this.scheme = scheme;
    }
    
    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
        if (!param.startsWith("//") || param.length() < 3) return text;
        return text.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, scheme + ":" + param));
    }

    @Override
    public @NotNull @LabelPattern String label() {
        return scheme;
    }
}
