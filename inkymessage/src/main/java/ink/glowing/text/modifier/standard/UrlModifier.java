package ink.glowing.text.modifier.standard;

import ink.glowing.text.Context;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

import static java.util.function.UnaryOperator.identity;

record UrlModifier(@NotNull @Subst("ms-web+lmao3") String scheme) implements Modifier {
    static final Modifier HTTP = new UrlModifier("http");
    static final Modifier HTTPS = new UrlModifier("https");

    @Override
    public @NotNull UnaryOperator<Component> prepareModification(@NotNull Arguments arguments, @NotNull Context context) {
        String param = arguments.parameter();
        if (!param.startsWith("//") || param.length() < 3) return identity();
        var clickEvent = ClickEvent.clickEvent(
                ClickEvent.Action.OPEN_URL,
                ClickEvent.Payload.string(scheme + ":" + param)
        );
        return text -> text.clickEvent(clickEvent);
    }

    @Override
    public @NotNull ArgumentValue.Type unknownArgumentType(@NotNull String parameter) {
        return ArgumentValue.Type.STRING;
    }

    @Override
    public @NotNull @LabelPattern String label() {
        return scheme;
    }
}
