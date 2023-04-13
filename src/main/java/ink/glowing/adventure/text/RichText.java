package ink.glowing.adventure.text;

import ink.glowing.adventure.modifier.Modifier;
import ink.glowing.adventure.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class RichText {
    public static final RichText EMPTY = new RichText("", List.of()) {
        @Override
        public @NotNull IncludedText render(Style style, List<RichText> richTexts) {
            return new IncludedText(Component.empty(), style);
        }
    };

    private final String text;
    private final List<Modifier.Prepared> modifiers;

    public RichText(@NotNull String text, @NotNull List<Modifier.Prepared> modifiers) {
        this.text = text;
        this.modifiers = modifiers;
    }

    public @NotNull IncludedText render(Style style, List<RichText> richTexts) {
        Component result = Component.empty();
        int lastAppend = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == 0) {
                result = result.append(text(text.substring(lastAppend, i)).style(style));
                int start = i + 1;
                //noinspection StatementWithEmptyBody
                while (text.charAt(++i) != 0);
                IncludedText included = richTexts.get(Integer.parseInt(text.substring(start, i))).render(style, richTexts);
                result = result.append(included.component());
                style = included.lastStyle();
                lastAppend = i + 1;
            } else if (ch == '&') {
                if (i + 1 == text.length()) continue;
                Style newStyle = AdventureUtils.mergeLegacyStyle(text.charAt(i + 1), style);
                if (newStyle != null) {
                    result = result.append(text(text.substring(lastAppend, i++)).style(style));
                    lastAppend = i + 1;
                    style = newStyle;
                }
            }
        }
        if (lastAppend != text.length()) {
            result = result.append(text(text.substring(lastAppend)).style(style));
        }
        for (var rawMod : modifiers) {
            result = rawMod.modify(result, richTexts);
        }
        return new IncludedText(result, style);
    }
}
