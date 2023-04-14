package ink.glowing.text.modifier;

import ink.glowing.text.RichText;
import net.kyori.adventure.key.Namespaced;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Modifier extends Namespaced {
    @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value);

    record Prepared(@NotNull Modifier modifier, @NotNull String param, @NotNull RichText value) {
        public @NotNull Component modify(@NotNull Component text, @NotNull List<RichText> includes) {
            return modifier.modify(text, param, value.render(includes).component());
        }
    }
}