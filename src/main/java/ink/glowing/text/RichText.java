package ink.glowing.text;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ink.glowing.text.utils.Utils.SECTION_CHAR;
import static net.kyori.adventure.text.Component.text;

public class RichText {
    public static final RichText EMPTY = new RichText("", List.of()) {
        @Override
        public @NotNull Style render(TextComponent.Builder globalBuilder, Style lastStyle, List<RichText> richTexts) {
            return lastStyle;
        }
    };

    private final String text;
    private final boolean hasSlashes;
    private final List<Modifier.Prepared> modifiers;

    public RichText(@NotNull String text, @NotNull List<Modifier.Prepared> modifiers) {
        this.text = text;
        this.hasSlashes = text.indexOf('\\') != -1;
        this.modifiers = modifiers;
    }

    public @NotNull Style render(TextComponent.Builder globalBuilder, Style lastStyle, List<RichText> richTexts) {
        var builder = Component.text();
        int lastAppend = 0;
        for (int index = 0; index < text.length(); index++) {
            char ch = text.charAt(index);
            if (ch == SECTION_CHAR) {
                builder.append(text(text.substring(lastAppend, index)).style(lastStyle));
                int start = index + 1;
                //noinspection StatementWithEmptyBody
                while (text.charAt(++index) != SECTION_CHAR);
                lastStyle = richTexts.get(Integer.parseInt(text.substring(start, index))).render(builder, lastStyle, richTexts);
                lastAppend = index + 1;
            } else if (ch == '&') {
                if (index + 1 == text.length() || Utils.isEscaped(text, index)) continue;
                char styleChar = text.charAt(index + 1);
                if (styleChar == '#') {
                    if (index + 8 >= text.length()) continue;
                    String colorStr = text.substring(index + 1, index + 8);
                    if (!Utils.isHexColor(colorStr)) continue;
                    if (lastAppend != index) appendPart(builder, lastAppend, index, lastStyle);
                    lastStyle = lastStyle.color(TextColor.fromHexString(colorStr));
                    lastAppend = (index += 7) + 1;
                } else {
                    Style newStyle = Utils.mergeLegacyStyle(styleChar, lastStyle);
                    if (newStyle == null) continue;
                    if (lastAppend != index) appendPart(builder, lastAppend, index, lastStyle);
                    lastStyle = newStyle;
                    lastAppend = (++index) + 1;
                }
            }
        }
        if (lastAppend < text.length()) {
            appendPart(builder, lastAppend, text.length(), lastStyle);
        }
        Component result = builder.build();
        for (var rawMod : modifiers) {
            result = rawMod.modify(result, richTexts);
        }
        globalBuilder.append(result);
        return lastStyle;
    }

    private void appendPart(@NotNull TextComponent.Builder builder, int start, int end, @NotNull Style style) {
        String substring = text.substring(start, end);
        if (hasSlashes) substring = InkyMessage.unescapeAll(substring);
        builder.append(text(substring).style(style));
    }
}
