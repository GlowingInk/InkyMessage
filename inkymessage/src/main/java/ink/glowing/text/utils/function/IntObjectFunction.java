package ink.glowing.text.utils.function;

/**
 * Function that accepts {@code int} and object arguments and produces a result.
 * @param <V> type of object argument
 * @param <R> type of result
 */
public interface IntObjectFunction<V, R> {
    /**
     * Applies this function to the given arguments.
     * @param i {@code int} argument
     * @param v object argument
     * @return function result
     */
    R apply(int i, V v);
}
