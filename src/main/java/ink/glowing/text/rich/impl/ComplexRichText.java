package ink.glowing.text.rich.impl;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.rich.RichText;
import ink.glowing.text.rich.TextContext;
import ink.glowing.text.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static ink.glowing.text.utils.Utils.SECTION_CHAR;
import static net.kyori.adventure.text.Component.text;

public class ComplexRichText implements RichText {
    private final String text;
    private final boolean hasSlashes;
    private final List<Modifier.Prepared> modifiers;

    public ComplexRichText(@NotNull String text, @NotNull List<Modifier.Prepared> modifiers) {
        this.text = text;
        this.hasSlashes = text.indexOf('\\') != -1;
        this.modifiers = modifiers;
    }

    @Override
    public @NotNull Component render(@NotNull TextContext context, @NotNull Consumer<Resulting> output) {
        TextComponent.Builder builder = Component.text();
        int[] totalLength = {0};
        int lastAppend = 0;
        for (int index = 0; index < text.length(); index++) {
            char ch = text.charAt(index);
            if (ch == SECTION_CHAR) {
                builder.append(text(text.substring(lastAppend, index)).style(context.lastStyle()));
                int start = index + 1;
                //noinspection StatementWithEmptyBody
                while (text.charAt(++index) != SECTION_CHAR);
                context.innerText(Integer.parseInt(text.substring(start, index))).render(context, rt -> {
                    builder.append(rt);
                    totalLength[0] += rt.length();
                });
                lastAppend = index + 1;
            } else if (ch == '&') {
                if (index + 1 == text.length() || Utils.isEscaped(text, index)) continue;
                char styleCh = text.charAt(index + 1);
                switch (styleCh) {
                    case '#' -> {
                        if (index + 8 >= text.length()) continue;
                        String colorStr = text.substring(index + 1, index + 8);
                        TextColor color = Utils.getHexColor(colorStr, false);
                        if (color == null) continue;
                        if (lastAppend != index) appendPart(builder, lastAppend, index, context.lastStyle(), totalLength);
                        context.lastStyle(context.lastStyle().color(color));
                        lastAppend = (index += 7) + 1;
                    }
                    case 'x' -> {
                        if (index + 14 >= text.length()) continue;
                        String colorStr = text.substring(index, index + 14);
                        TextColor color = Utils.getHexColor(colorStr, true);
                        if (color == null) continue;
                        if (lastAppend != index) appendPart(builder, lastAppend, index, context.lastStyle(), totalLength);
                        context.lastStyle(context.lastStyle().color(color));
                        lastAppend = (index += 13) + 1;
                    }
                    default -> {
                        Style newStyle = context.styleResolver().mergeCharacterStyle(styleCh, context.lastStyle());
                        if (newStyle == null) continue;
                        if (lastAppend != index) appendPart(builder, lastAppend, index, context.lastStyle(), totalLength);
                        context.lastStyle(newStyle);
                        lastAppend = (++index) + 1;
                    }
                }
            }
        }
        if (lastAppend < text.length()) {
            appendPart(builder, lastAppend, text.length(), context.lastStyle(), totalLength);
        }
        Resulting resulting = new Resulting(builder.build(), totalLength[0]);
        for (var rawMod : modifiers) {
            resulting.component(rawMod.modify(resulting, context));
        }
        output.accept(resulting);
        return resulting.asComponent();
    }

    private void appendPart(@NotNull TextComponent.Builder builder, int start, int end, @NotNull Style style, int[] totalLength) {
        String substring = text.substring(start, end);
        if (hasSlashes) substring = InkyMessage.unescapeAll(substring);
        totalLength[0] += substring.length();
        builder.append(text(substring).style(style));
    }
}
