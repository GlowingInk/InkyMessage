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
import static ink.glowing.text.style.modifier.internal.LangModifiers.argModifier;
import static ink.glowing.text.style.modifier.internal.LangModifiers.fallbackModifier;

final class Stringifier {
    private Stringifier() {}

    public static @NotNull String stringify(@NotNull Component text, @NotNull InkyMessage.Resolver resolver) {
        StringBuilder builder = new StringBuilder();
        stringify(builder, new TreeSet<>(), text, resolver, new boolean[]{false});
        return builder.toString();
    }

    // TODO Use ComponentFlattener?
    private static void stringify(
            @NotNull StringBuilder builder,
            final @NotNull TreeSet<SymbolicStyle> outerStyle,
            @NotNull Component text,
            @NotNull InkyMessage.Resolver resolver,
            boolean @NotNull [] previousStyled
    ) {
        var modifiers = resolver.readStyleModifiers(text);
        if (!modifiers.isEmpty()) {
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
        appendComponent(builder, text, resolver);
        var children = text.children();
        var newOuterStyle = new TreeSet<>(outerStyle);
        newOuterStyle.addAll(currentStyle);
        for (var child : children) {
            stringify(builder, newOuterStyle, child, resolver, previousStyled);
        }
        if (!modifiers.isEmpty()) {
            builder.append("]");
            for (var modifier : modifiers) {
                builder.append(modifier);
            }
        }
    }

    private static void appendComponent(
            @NotNull StringBuilder builder,
            @NotNull Component component,
            @NotNull InkyMessage.Resolver resolver
    ) {
        if (component instanceof TextComponent text) {
            builder.append(escape(text.content()));
        } else if (component instanceof TranslatableComponent translatable) {
            builder.append("&{lang:").append(translatable.key()).append("}");
            for (var modifier : argModifier().read(resolver, translatable)) {
                builder.append(modifier);
            }
            for (var modifier : fallbackModifier().read(resolver, translatable)) {
                builder.append(modifier);
            }
        } else if (component instanceof KeybindComponent keybind) {
            builder.append("&{key:").append(keybind.keybind()).append("}"); // TODO implement
        } else if (component instanceof ScoreComponent score) {
            builder.append(score.objective()); // TODO implement
        } else if (component instanceof SelectorComponent selector) {
            builder.append(selector.pattern()); // TODO implement
        } else {
            builder.append("?");
        }
    }
}
