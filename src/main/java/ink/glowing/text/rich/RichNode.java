package ink.glowing.text.rich;

import ink.glowing.text.rich.impl.ComplexRichNode;
import ink.glowing.text.rich.impl.ComponentRichNode;
import ink.glowing.text.rich.impl.EmptyRichNode;
import ink.glowing.text.rich.impl.LiteralRichNode;
import ink.glowing.text.style.tag.StyleTag;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public interface RichNode {
    @ApiStatus.Internal
    char SECTION_CHAR = '§';
    @ApiStatus.Internal
    String SECTION = Character.toString(SECTION_CHAR);

    @NotNull Component render(@NotNull BuildContext context);

    static @NotNull RichNode emptyNode() {
        return EmptyRichNode.instance();
    }

    static @NotNull RichNode componentNode(@NotNull Component component) {
        return new ComponentRichNode(component);
    }

    static @NotNull RichNode literalNode(@NotNull String text) {
        return literalNode(text, List.of());
    }

    static @NotNull RichNode literalNode(@NotNull String text, @NotNull List<StyleTag.Prepared> tags) {
        return new LiteralRichNode(text, tags);
    }

    static @NotNull RichNode node(@NotNull String text) {
        return node(text, List.of());
    }

    static @NotNull RichNode node(@NotNull String text, @NotNull List<StyleTag.Prepared> tags) {
        if (text.isEmpty()) return emptyNode();
        return new ComplexRichNode(text, tags);
    }

    @ApiStatus.Internal
    static @NotNull String nodeId(int i) {
        return SECTION + i + SECTION;
    }
}
