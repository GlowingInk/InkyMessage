package ink.glowing.text.utils.function;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Operation that accepts one {@code float} argument and returns no result.
 */
@FunctionalInterface
public interface FloatConsumer {
    /**
     * Performs this operation on the given value.
     * @param value input value
     */
    void accept(float value);

    /**
     * Returns a consumer that performs this operation followed by the {@code after} operation.
     * @param after operation to perform after this one
     * @return composed consumer
     */
    @Contract(value = "_ -> new", pure = true)
    default @NotNull FloatConsumer andThen(@NotNull FloatConsumer after) {
        return (t) -> {
            this.accept(t);
            after.accept(t);
        };
    }
}
