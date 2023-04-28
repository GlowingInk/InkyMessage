package ink.glowing.text;

import ink.glowing.text.style.symbolic.SymbolicStyle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.TreeSet;

import static ink.glowing.text.InkyMessage.escape;

final class SerializerImpl {
    private SerializerImpl() {}

    public static @NotNull String serialize(@NotNull Component text, @NotNull InkyMessage.Resolver resolver) {
        StringBuilder builder = new StringBuilder();
        serialize(builder, new TreeSet<>(), text, resolver, new boolean[]{false});
        return builder.toString();
    }

    private static void serialize(
            @NotNull StringBuilder builder,
            final @NotNull TreeSet<SymbolicStyle> outerStyle,
            @NotNull Component text,
            @NotNull InkyMessage.Resolver resolver,
            boolean @NotNull [] previousStyled
    ) {
        var tags = resolver.readStyleTags(text);
        if (!tags.isEmpty()) {
            builder.append("&[");
        }
        var currentStyle = resolver.readSymbolics(text);
        if (previousStyled[0] && (currentStyle.isEmpty() || !currentStyle.first().resets())) {
            if (outerStyle.isEmpty()) {
                builder.append(resolver.symbolicReset().asFormatted());
            } else for (var symb : outerStyle) {
                builder.append(symb.asFormatted());
            }
        }
        if (currentStyle.isEmpty()) {
            previousStyled[0] = false;
        } else {
            previousStyled[0] = true;
            for (var symb : currentStyle) {
                builder.append(symb.asFormatted());
            }
        }

        builder.append(asString(text, resolver));
        var children = text.children();
        var newOuterStyle = new TreeSet<>(outerStyle);
        newOuterStyle.addAll(currentStyle);
        for (var child : children) {
            serialize(builder, newOuterStyle, child, resolver, previousStyled);
        }
        if (!tags.isEmpty()) {
            builder.append("]");
            for (var tag : tags) {
                builder.append(tag);
            }
        }
    }

    private static String asString(@NotNull Component component, @NotNull InkyMessage.Resolver resolver) {
        if (component instanceof TextComponent text) {
            return escape(text.content());
        } else if (component instanceof TranslatableComponent translatable) {
            StringBuilder builder = new StringBuilder("&{lang:" + translatable.key() + "}");
            for (var arg : translatable.args()) {
                builder.append("(arg:").append(serialize(arg, resolver)).append(')');
            }
            if (translatable.fallback() != null) {
                builder.append("(fallback:").append(escape(translatable.fallback())).append(')');
            }
            return builder.toString();
        } else if (component instanceof KeybindComponent keybind) {
            return "&{key:" + keybind.keybind() + "}"; // TODO implement
        } else if (component instanceof ScoreComponent score) {
            return score.objective(); // TODO implement
        } else if (component instanceof SelectorComponent selector) {
            return selector.pattern(); // TODO implement
        } else {
            return "?";
        }
    }
}
