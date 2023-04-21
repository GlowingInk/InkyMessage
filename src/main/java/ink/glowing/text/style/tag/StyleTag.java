package ink.glowing.text.style.tag;

import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.utils.Prefixed;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface StyleTag extends Prefixed {
    @NotNull Component modify(@NotNull BuildContext context, @NotNull Component text, @NotNull String param, @NotNull String value);

    record Prepared(@NotNull StyleTag styleTag, @NotNull String param, @NotNull String value) {
        public @NotNull Component modify(@NotNull Component text, @NotNull BuildContext context) {
            return styleTag.modify(context, text, param, value);
        }
    }
}
