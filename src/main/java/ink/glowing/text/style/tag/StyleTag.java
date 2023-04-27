package ink.glowing.text.style.tag;

import ink.glowing.text.InkyMessageResolver;
import ink.glowing.text.rich.BuildContext;
import net.kyori.adventure.key.Namespaced;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface StyleTag extends Namespaced {
    @NotNull Component modify(@NotNull BuildContext context, @NotNull Component text, @NotNull String param, @NotNull String value);

    @NotNull @Unmodifiable List<Prepared> read(@NotNull InkyMessageResolver resolver, @NotNull Component text);

    record Prepared(@NotNull StyleTag styleTag, @NotNull String param, @NotNull String value) {
        public @NotNull Component modify(@NotNull Component text, @NotNull BuildContext context) {
            return styleTag.modify(context, text, param, value);
        }

        @Override
        public String toString() {
            String result = styleTag.namespace();
            if (!param.isEmpty()) result += ":" + param;
            if (!value.isEmpty()) result += " " + value;
            return "(" + result + ")";
        }
    }
}
