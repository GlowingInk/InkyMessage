package ink.glowing.text.placeholder;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

record InlinedPlaceholderImpl(
        @NotNull String name,
        @NotNull Function<@NotNull String, @NotNull String> resultFunct
) implements Placeholder.Inlined {
    @Override
    public @NotNull String parseInlined(@NotNull String value) {
        return resultFunct.apply(value);
    }
}
