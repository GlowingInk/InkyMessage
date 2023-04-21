package ink.glowing.text.rich;

import ink.glowing.text.InkyMessageResolver;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuildContext {
    private final List<RichNode> innerTexts;
    private final InkyMessageResolver inkyResolver;
    private Style lastStyle;

    public BuildContext(@NotNull List<RichNode> innerTexts, @NotNull InkyMessageResolver inkyResolver) {
        this.innerTexts = innerTexts;
        this.inkyResolver = inkyResolver;
        this.lastStyle = Style.empty();
    }

    public @NotNull BuildContext colorlessCopy() {
        return new BuildContext(innerTexts, inkyResolver);
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

    public @NotNull List<RichNode> innerTexts() {
        return innerTexts;
    }

    public int innerTextsCount() {
        return innerTexts.size();
    }

    public int innerTextAdd(@NotNull RichNode richNode) {
        innerTexts.add(richNode);
        return innerTexts.size() - 1;
    }

    public @NotNull InkyMessageResolver inkyResolver() {
        return inkyResolver;
    }
}
