package ink.glowing.text.modifier;

import ink.glowing.text.rich.RichText;
import ink.glowing.text.rich.TextContext;
import ink.glowing.text.rich.impl.EmptyRichText;
import net.kyori.adventure.key.Namespaced;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Modifier extends Namespaced {
    @NotNull Component modify(@NotNull RichText.Resulting text, @NotNull String param, @NotNull Component value);

    record Prepared(@NotNull Modifier modifier, @NotNull String param, @NotNull RichText value) {
        public @NotNull Component modify(@NotNull RichText.Resulting text, @NotNull TextContext context) {
            if (value instanceof EmptyRichText) {
                return modifier.modify(text, param, Component.empty());
            } else {
                return modifier.modify(text, param, value.render(context.colorlessCopy()));
            }
        }
    }
}
