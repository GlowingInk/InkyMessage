package ink.glowing.text.rich.impl;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.rich.RichNode;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ink.glowing.text.utils.Utils.SECTION_CHAR;
import static net.kyori.adventure.text.Component.text;

public class ComplexRichNode implements RichNode {
    private final String textStr;
    private final boolean hasSlashes;
    private final List<StyleTag.Prepared> tags;

    public ComplexRichNode(@NotNull String textStr, @NotNull List<StyleTag.Prepared> tags) {
        this.textStr = textStr;
        this.hasSlashes = textStr.indexOf('\\') != -1;
        this.tags = tags;
    }

    @Override
    public @NotNull Component render(@NotNull BuildContext context) {
        TextComponent.Builder builder = Component.text();
        int lastAppend = 0;
        for (int index = 0; index < textStr.length(); index++) {
            char ch = textStr.charAt(index);
            if (ch == SECTION_CHAR) {
                if (lastAppend != index) appendPart(builder, lastAppend, index, context.lastStyle());
                int start = index + 1;
                //noinspection StatementWithEmptyBody
                while (textStr.charAt(++index) != SECTION_CHAR);
                builder.append(context.innerText(Integer.parseInt(textStr.substring(start, index))).render(context));
                lastAppend = index + 1;
            } else if (ch == '&') {
                if (index + 1 == textStr.length() || InkyMessage.isEscaped(textStr, index)) continue;
                char styleCh = textStr.charAt(index + 1);
                switch (styleCh) {
                    case '#' -> {
                        if (index + 8 >= textStr.length()) continue;
                        String colorStr = textStr.substring(index + 1, index + 8);
                        TextColor color = Utils.getHexColor(colorStr, false);
                        if (color == null) continue;
                        if (lastAppend != index) appendPart(builder, lastAppend, index, context.lastStyle());
                        context.lastStyle(context.lastStyle().color(color));
                        lastAppend = (index += 7) + 1;
                    }
                    case 'x' -> {
                        if (index + 14 >= textStr.length()) continue;
                        String colorStr = textStr.substring(index, index + 14);
                        TextColor color = Utils.getHexColor(colorStr, true);
                        if (color == null) continue;
                        if (lastAppend != index) appendPart(builder, lastAppend, index, context.lastStyle());
                        context.lastStyle(context.lastStyle().color(color));
                        lastAppend = (index += 13) + 1;
                    }
                    default -> {
                        Style newStyle = context.styleResolver().mergeSymbolicStyle(styleCh, context.lastStyle());
                        if (newStyle == null) continue;
                        if (lastAppend != index) appendPart(builder, lastAppend, index, context.lastStyle());
                        context.lastStyle(newStyle);
                        lastAppend = (++index) + 1;
                    }
                }
            }
        }
        if (lastAppend < textStr.length()) {
            appendPart(builder, lastAppend, textStr.length(), context.lastStyle());
        }
        Component result = builder.build();
        for (var preparedTag : tags) {
            result = preparedTag.modify(result, context);
        }
        return result;
    }

    private void appendPart(@NotNull TextComponent.Builder builder, int start, int end, @NotNull Style style) {
        String substring = textStr.substring(start, end);
        if (hasSlashes) substring = InkyMessage.unescape(substring);
        builder.append(text(substring).style(style));
    }
}
