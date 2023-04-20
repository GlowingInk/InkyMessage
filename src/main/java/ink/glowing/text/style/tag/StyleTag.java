package ink.glowing.text.style.tag;

import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.rich.RichNode;
import ink.glowing.text.rich.impl.EmptyRichNode;
import ink.glowing.text.style.StyleModifier;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface StyleTag extends StyleModifier {
    @Override
    default @NotNull Component modify(@NotNull Component text, @NotNull String param) {
        return modify(text, param, Component.empty());
    }

    @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value);

    record Prepared(@NotNull StyleTag styleTag, @NotNull String param, @NotNull RichNode value) {
        public @NotNull Component modify(@NotNull Component text, @NotNull BuildContext context) {
            if (value instanceof EmptyRichNode) {
                return styleTag.modify(text, param);
            } else {
                return styleTag.modify(text, param, value.render(context.colorlessCopy()));
            }
        }
    }
}
