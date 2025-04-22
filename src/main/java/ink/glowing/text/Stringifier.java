package ink.glowing.text;

import ink.glowing.text.symbolic.SymbolicStyle;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

import static ink.glowing.text.InkyMessage.escape;
import static ink.glowing.text.modifier.standard.StandardModifiers.*;
import static net.kyori.adventure.text.format.Style.style;

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
                for (var modifier : langArgModifier().read(translatable, inkyMessage)) {
                    builder.append(modifier);
                }
                for (var modifier : langFallbackModifier().read(translatable, inkyMessage)) {
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
                for (var modifier : selectorSeparatorModifier().read(selector, inkyMessage)) {
                    builder.append(modifier);
                }
            }

            default -> builder.append('?');
        }
    }

    private static @NotNull TreeSet<SymbolicStyle> pullSymbolics(@NotNull Style style, @NotNull InkyMessage inkyMessage) {
        // TODO StyleBuilder?
        TreeSet<SymbolicStyle> found = new TreeSet<>();
        for (var symb : inkyMessage.symbolics().values()) {
            if (symb.isApplied(style)) {
                style = style.unmerge(symb.base());
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
            modifiers.addAll(modifier.read(text, inkyMessage));
        }
        return modifiers;
    }

    private static final class HexSymbolicStyle implements SymbolicStyle {
        private final @NotNull TextColor color;
        private final @NotNull Style cleanStyle;

        private HexSymbolicStyle(@NotNull TextColor color) {
            this.color = color;
            this.cleanStyle = style(color);
        }

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
        public @NotNull Style base() {
            return cleanStyle;
        }

        @Override
        public @NotNull String asFormatted() {
            return "&" + color.asHexString();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (HexSymbolicStyle) obj;
            return Objects.equals(this.color, that.color) &&
                    Objects.equals(this.cleanStyle, that.cleanStyle);
        }

        @Override
        public int hashCode() {
            return Objects.hash(color, cleanStyle);
        }

        @Override
        public String toString() {
            return "HexSymbolicStyle[" +
                    "color=" + color + ", " +
                    "cleanStyle=" + cleanStyle +
                    ']';
        }
    }
}
