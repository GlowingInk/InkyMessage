package ink.glowing.text;

import ink.glowing.text.symbolic.SymbolicStyle;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static ink.glowing.text.InkyMessage.escape;
import static ink.glowing.text.modifier.standard.StandardModifiers.*;
import static ink.glowing.text.symbolic.standard.StandardSymbolicStyles.resetDecorations;

@ApiStatus.Internal
final class Stringifier { private Stringifier() {}
    public static @NotNull String stringify(@NotNull Component text, @NotNull InkyMessage inkyMessage) {
        StringBuilder builder = new StringBuilder();
        stringify(builder, new TreeSet<>(), text, inkyMessage, new boolean[]{false});
        return builder.toString();
    }

    // TODO Use ComponentFlattener?
    private static void stringify(
            @NotNull StringBuilder builder,
            final @NotNull TreeSet<SymbolicStyle> outerStyle,
            @NotNull Component text,
            @NotNull InkyMessage inkyMessage,
            boolean @NotNull [] previousStyled
    ) {
        var modifiers = pullModifiers(text, inkyMessage);
        if (!modifiers.isEmpty()) {
            builder.append("&[");
        }
        var currentStyle = pullSymbolics(text.style(), inkyMessage);
        if (previousStyled[0] && (currentStyle.isEmpty() || !currentStyle.first().resets())) {
            if (outerStyle.isEmpty()) {
                builder.append(inkyMessage.symbolicReset().asFormatted());
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
        appendComponent(builder, text, inkyMessage);
        var children = text.children();
        var newOuterStyle = new TreeSet<>(outerStyle);
        newOuterStyle.addAll(currentStyle);
        for (var child : children) {
            stringify(builder, newOuterStyle, child, inkyMessage, previousStyled);
        }
        if (!modifiers.isEmpty()) {
            builder.append(']');
            for (var modifier : modifiers) {
                builder.append(modifier);
            }
        }
    }

    private static void appendComponent(
            @NotNull StringBuilder builder,
            @NotNull Component component,
            @NotNull InkyMessage inkyMessage
    ) {
        switch (component) {
            case TextComponent text -> builder.append(escape(text.content()));

            case TranslatableComponent translatable -> {
                builder
                        .append("&{lang:")
                        .append(escape(translatable.key()))
                        .append("}");
                for (var modifier : langArgModifier().readModifier(translatable, inkyMessage)) {
                    builder.append(modifier);
                }
                for (var modifier : langFallbackModifier().readModifier(translatable, inkyMessage)) {
                    builder.append(modifier);
                }
            }

            case KeybindComponent keybind -> builder
                    .append("&{keybind:")
                    .append(escape(keybind.keybind()))
                    .append('}');

            case ScoreComponent score -> builder
                    .append("&{score:")
                    .append(escape(score.name()))
                    .append(' ')
                    .append(escape(score.objective()))
                    .append('}');

            case SelectorComponent selector -> {
                builder
                        .append("&{selector:")
                        .append(escape(selector.pattern()))
                        .append('}');
                for (var modifier : selectorSeparatorModifier().readModifier(selector, inkyMessage)) {
                    builder.append(modifier);
                }
            }

            default -> builder.append('?');
        }
    }

    private static @NotNull TreeSet<SymbolicStyle> pullSymbolics(@NotNull Style style, @NotNull InkyMessage inkyMessage) {
        TreeSet<SymbolicStyle> found = new TreeSet<>();
        for (var symb : inkyMessage.symbolics().values()) {
            if (symb.isApplied(style)) {
                style = symb.unmerge(style);
                found.add(symb);
                if (style.isEmpty()) return found;
            }
        }
        if (style.color() != null) { // If color is still merged
            found.add(new HexSymbolicStyle(style.color()));
        }
        return found;
    }

    private static @NotNull List<String> pullModifiers(@NotNull Component text, @NotNull InkyMessage inkyMessage) {
        List<String> modifiers = new ArrayList<>();
        for (var modifier : inkyMessage.modifiers().values()) {
            modifiers.addAll(modifier.readModifier(text, inkyMessage));
        }
        return modifiers;
    }

    private record HexSymbolicStyle(@NotNull TextColor color) implements SymbolicStyle {
        @Override
        public char symbol() {
                return '#';
        }

        @Override
        public boolean resets() {
            return true;
        }

        @Override
        public boolean isApplied(@NotNull Style at) {
            return color.equals(at.color());
        }

        @Override
        public @NotNull Style merge(@NotNull Style other) {
            return other.decorations(resetDecorations()).color(color);
        }

        @Override
        public @NotNull Style unmerge(@NotNull Style other) {
            return other.color(null);
        }

        @Override
        public @NotNull String asFormatted() {
            return "&" + color.asHexString();
        }
    }
}
