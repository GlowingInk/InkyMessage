package ink.glowing.text.style.tag;

import ink.glowing.text.InkyMessage;
import net.kyori.adventure.key.Namespaced;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public sealed interface StyleTag<T> extends Namespaced {
    @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull T value);

    @NotNull @Unmodifiable List<String> read(@NotNull InkyMessage.Resolver resolver, @NotNull Component text);

    non-sealed interface Plain extends StyleTag<String> {}

    non-sealed interface Complex extends StyleTag<Component> {}

    default @NotNull String asFormatted(@NotNull String param, @NotNull String value) {
            String result = namespace();
            if (!param.isEmpty()) result += ":" + param;
            if (!value.isEmpty()) result += " " + value;
            return "(" + result + ")";
    }

    default @NotNull Prepared<T> asPrepared(@NotNull String param, @NotNull T value) {
        return new Prepared<>(this, param, value);
    }

    record Prepared<T>(@NotNull StyleTag<T> tag, @NotNull String param, @NotNull T value) {
        public @NotNull Component modify(@NotNull Component text) {
            return tag.modify(text, param, value);
        }
    }
}
