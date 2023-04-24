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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ink.glowing.text.utils.Utils.SECTION_CHAR;
import static net.kyori.adventure.text.Component.text;

@ApiStatus.Internal
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
        String input = context.inkyResolver().applyReplacers(this.textStr, context.innerTexts());
        TextComponent.Builder builder = Component.text();
        int lastAppend = 0;
        for (int index = 0; index < input.length(); index++) {
            char ch = input.charAt(index);
            if (ch == SECTION_CHAR) {
                if (lastAppend != index) appendPart(input, builder, lastAppend, index, context.lastStyle());
                int start = index + 1;
                //noinspection StatementWithEmptyBody
                while (input.charAt(++index) != SECTION_CHAR);
                builder.append(context.innerText(Integer.parseInt(input.substring(start, index))).render(context));
                lastAppend = index + 1;
            } else if (ch == '&') {
                if (index + 1 == input.length() || InkyMessage.isEscaped(input, index)) continue;
                char styleCh = input.charAt(index + 1);
                switch (styleCh) {
                    case '#' -> {
                        if (index + 8 >= input.length()) continue;
                        String colorStr = input.substring(index + 1, index + 8);
                        TextColor color = Utils.getHexColor(colorStr, false);
                        if (color == null) continue;
                        if (lastAppend != index) appendPart(input, builder, lastAppend, index, context.lastStyle());
                        context.lastStyle(context.lastStyle().color(color));
                        lastAppend = (index += 7) + 1;
                    }
                    case 'x' -> {
                        if (index + 14 >= input.length()) continue;
                        String colorStr = input.substring(index, index + 14);
                        TextColor color = Utils.getHexColor(colorStr, true);
                        if (color == null) continue;
                        if (lastAppend != index) appendPart(input, builder, lastAppend, index, context.lastStyle());
                        context.lastStyle(context.lastStyle().color(color));
                        lastAppend = (index += 13) + 1;
                    }
                    default -> {
                        Style newStyle = context.inkyResolver().applySymbolicStyle(styleCh, context.lastStyle());
                        if (newStyle == null) continue;
                        if (lastAppend != index) appendPart(input, builder, lastAppend, index, context.lastStyle());
                        context.lastStyle(newStyle);
                        lastAppend = (++index) + 1;
                    }
                }
            }
        }
        if (lastAppend < input.length()) {
            appendPart(input, builder, lastAppend, input.length(), context.lastStyle());
        }
        Component result = builder.build();
        for (var preparedTag : tags) {
            result = preparedTag.modify(result, context);
        }
        return result;
    }

    private void appendPart(@NotNull String input, @NotNull TextComponent.Builder builder, int start, int end, @NotNull Style style) {
        String substring = input.substring(start, end);
        if (hasSlashes) substring = InkyMessage.unescape(substring);
        builder.append(text(substring).style(style));
    }
}
