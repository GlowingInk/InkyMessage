package ink.glowing.text.utils.function;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Function that accepts one {@code float} argument and produces a result.
 * @param <R> type of result
 */
@FunctionalInterface
public interface FloatFunction<R> {
    /**
     * Applies this function to the given value.
     * @param value input value
     * @return function result
     */
    R apply(float value);

    /**
     * Returns a function that first applies {@code before} and then this function.
     * @param before function to apply before this one
     * @param <V> type of input of composed function
     * @return composed function
     */
    @Contract(value = "_ -> new", pure = true)
    default <V> Function<V, R> compose(@NotNull ToFloatFunction<? super V> before) {
        return (v) -> this.apply(before.applyAsFloat(v));
    }

    /**
     * Returns a function that first applies this function and then {@code after}.
     * @param after function to apply after this one
     * @param <V> type of result of composed function
     * @return composed function
     */
    @Contract(value = "_ -> new", pure = true)
    default <V> FloatFunction<V> andThen(@NotNull Function<? super R, ? extends V> after) {
        return (t) -> after.apply(this.apply(t));
    }
}
