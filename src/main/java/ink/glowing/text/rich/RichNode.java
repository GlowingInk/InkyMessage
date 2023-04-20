package ink.glowing.text.rich;

import ink.glowing.text.rich.impl.ComplexRichNode;
import ink.glowing.text.rich.impl.EmptyRichNode;
import ink.glowing.text.style.tag.StyleTag;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RichNode {
    @NotNull Component render(@NotNull BuildContext context);

    static @NotNull RichNode empty() {
        return EmptyRichNode.emptyRichText();
    }

    static @NotNull RichNode richText(@NotNull String text, @NotNull List<StyleTag.Prepared> tags) {
        if (text.isEmpty()) return empty();
        return new ComplexRichNode(text, tags);
    }
}
