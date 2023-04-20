package ink.glowing.text.rich;

import ink.glowing.text.style.StyleResolver;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuildContext {
    private final List<RichNode> innerTexts;
    private final StyleResolver styleResolver;
    private Style lastStyle;

    public BuildContext(@NotNull List<RichNode> innerTexts, @NotNull StyleResolver styleResolver) {
        this.innerTexts = innerTexts;
        this.styleResolver = styleResolver;
        this.lastStyle = Style.empty();
    }

    public @NotNull BuildContext colorlessCopy() {
        return new BuildContext(innerTexts, styleResolver);
    }

    public @NotNull Style lastStyle() {
        return lastStyle;
    }

    public void lastStyle(@NotNull Style lastStyle) {
        this.lastStyle = lastStyle;
    }

    public @NotNull RichNode innerText(int index) {
        return innerTexts.get(index);
    }

    public int innerTextsCount() {
        return innerTexts.size();
    }

    public int innerTextAdd(@NotNull RichNode richNode) {
        innerTexts.add(richNode);
        return innerTexts.size() - 1;
    }

    public @NotNull StyleResolver styleResolver() {
        return styleResolver;
    }
}
