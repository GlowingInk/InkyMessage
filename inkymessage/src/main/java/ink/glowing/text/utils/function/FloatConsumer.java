package ink.glowing.text.utils.function;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface FloatConsumer {
    void accept(float value);

    @Contract(value = "_ -> new", pure = true)
    default @NotNull FloatConsumer andThen(@NotNull FloatConsumer after) {
        return (t) -> {
            this.accept(t);
            after.accept(t);
        };
    }
}
