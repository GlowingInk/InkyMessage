package ink.glowing.adventure.text;

import ink.glowing.adventure.modifier.Modifier;
import ink.glowing.adventure.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ink.glowing.adventure.InkyMessage.SPECIAL_CHAR;
import static net.kyori.adventure.text.Component.text;

public class RichText {
    public static final RichText EMPTY = new RichText("", List.of()) {
        @Override
        public @NotNull IncludedText render(List<RichText> richTexts) {
            return new IncludedText(Component.empty(), Style.empty());
        }
    };

    private final String text;
    private final List<Modifier.Prepared> modifiers;

    public RichText(@NotNull String text, @NotNull List<Modifier.Prepared> modifiers) {
        this.text = text;
        this.modifiers = modifiers;
    }

    public @NotNull IncludedText render(List<RichText> richTexts) {
        Style lastStyle = Style.empty();
        Component result = Component.empty();
        int lastAppend = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == SPECIAL_CHAR) {
                result = result.append(text(text.substring(lastAppend, i)).style(lastStyle));
                int start = i + 1;
                //noinspection StatementWithEmptyBody
                while (text.charAt(++i) != SPECIAL_CHAR);
                IncludedText included = richTexts.get(Integer.parseInt(text.substring(start, i))).render(richTexts);
                result = result.append(included.component());
                lastStyle = included.lastStyle();
                lastAppend = i + 1;
            } else if (ch == '&') {
                if (i + 1 == text.length()) continue;
                char styleChar = text.charAt(i + 1);
                if (styleChar == '#' && i + 8 < text.length()) {
                    String colorStr = text.substring(i + 1, i + 8);
                    if (!AdventureUtils.isHexColor(colorStr)) continue;
                    result = result.append(text(text.substring(lastAppend, i)).style(lastStyle));
                    lastStyle = lastStyle.color(TextColor.fromHexString(colorStr));
                    lastAppend = (i += 7) + 1;
                } else {
                    Style newStyle = AdventureUtils.mergeLegacyStyle(styleChar, lastStyle);
                    if (newStyle == null) continue;
                    result = result.append(text(text.substring(lastAppend, i)).style(lastStyle));
                    lastStyle = newStyle;
                    lastAppend = (++i) + 1;
                }
            }
        }
        if (lastAppend < text.length()) {
            result = result.append(text(text.substring(lastAppend)).style(lastStyle));
        }
        for (var rawMod : modifiers) {
            result = rawMod.modify(result, richTexts);
        }
        return new IncludedText(result, lastStyle);
    }
}
