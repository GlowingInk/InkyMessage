package ink.glowing.text.rich;

import ink.glowing.text.modifier.StyleResolver;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GlobalContext {
    private final List<RichText> innerTexts;
    private final StyleResolver styleResolver;
    private Style lastStyle;

    public GlobalContext(@NotNull List<RichText> innerTexts, @NotNull StyleResolver styleResolver) {
        this.innerTexts = innerTexts;
        this.styleResolver = styleResolver;
        this.lastStyle = Style.empty();
    }

    public @NotNull GlobalContext colorlessCopy() {
        return new GlobalContext(innerTexts, styleResolver);
    }

    public @NotNull Style lastStyle() {
        return lastStyle;
    }

    public void lastStyle(@NotNull Style lastStyle) {
        this.lastStyle = lastStyle;
    }

    public @NotNull RichText innerText(int index) {
        return innerTexts.get(index);
    }

    public int innerTextsCount() {
        return innerTexts.size();
    }

    public int innerTextAdd(@NotNull RichText richText) {
        innerTexts.add(richText);
        return innerTexts.size() - 1;
    }

    public @NotNull StyleResolver styleResolver() {
        return styleResolver;
    }
}
