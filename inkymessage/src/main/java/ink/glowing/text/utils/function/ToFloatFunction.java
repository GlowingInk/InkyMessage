package ink.glowing.text.utils.function;

/**
 * Function that produces a {@code float} result.
 * @param <V> type of input
 */
public interface ToFloatFunction<V> {
    /**
     * Applies this function to the given argument.
     * @param v input value
     * @return function result
     */
    float applyAsFloat(V v);
}
