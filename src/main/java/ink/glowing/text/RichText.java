package ink.glowing.text;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ink.glowing.text.utils.Utils.SECTION_CHAR;
import static net.kyori.adventure.text.Component.text;

public class RichText {
    public static final RichText EMPTY = new RichText("", List.of()) {
        @Override
        public @NotNull RichText.Included render(List<RichText> richTexts) {
            return new Included(Component.empty(), Style.empty());
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

    public @NotNull RichText.Included render(List<RichText> richTexts) {
        Style lastStyle = Style.empty();
        Component result = Component.empty();
        int lastAppend = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == SECTION_CHAR) {
                result = result.append(text(text.substring(lastAppend, i)).style(lastStyle));
                int start = i + 1;
                //noinspection StatementWithEmptyBody
                while (text.charAt(++i) != SECTION_CHAR);
                Included included = richTexts.get(Integer.parseInt(text.substring(start, i))).render(richTexts);
                result = result.append(included.component());
                if (!included.lastStyle().isEmpty()) lastStyle = included.lastStyle();
                lastAppend = i + 1;
            } else if (ch == '&') {
                if (i + 1 == text.length() || Utils.isEscaped(text, i)) continue;
                char styleChar = text.charAt(i + 1);
                if (styleChar == '#' && i + 8 < text.length()) {
                    String colorStr = text.substring(i + 1, i + 8);
                    if (!Utils.isHexColor(colorStr)) continue;
                    result = makeComponent(result, lastAppend, i, lastStyle);
                    lastStyle = lastStyle.color(TextColor.fromHexString(colorStr));
                    lastAppend = (i += 7) + 1;
                } else {
                    Style newStyle = Utils.mergeLegacyStyle(styleChar, lastStyle);
                    if (newStyle == null) continue;
                    result = makeComponent(result, lastAppend, i, lastStyle);
                    lastStyle = newStyle;
                    lastAppend = (++i) + 1;
                }
            }
        }
        if (lastAppend < text.length()) {
            result = makeComponent(result, lastAppend, text.length(), lastStyle);
        }
        for (var rawMod : modifiers) {
            result = rawMod.modify(result, richTexts);
        }
        return new Included(result, lastStyle);
    }

    private @NotNull Component makeComponent(Component comp, int start, int end, Style style) {
        String substring = text.substring(start, end);
        if (hasSlashes) substring = InkyMessage.unescapeAll(substring);
        return comp.append(text(substring).style(style));
    }

    private record Included(@NotNull Component component, @NotNull Style lastStyle) {}
}
