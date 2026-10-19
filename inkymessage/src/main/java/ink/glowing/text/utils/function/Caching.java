package ink.glowing.text.utils.function;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility methods for creating caching functions.
 */
public final class Caching {
    private Caching() { }

    /**
     * Wraps function so its results are cached per input in a {@link HashMap}.
     * @param function function to wrap
     * @param <T> type of input
     * @param <R> type of result
     * @return caching function
     */
    public static <T, R> @NotNull Function<T, R> caching(@NotNull Function<T, R> function) {
        return new CFunction<>(function, new HashMap<T, R>()::computeIfAbsent);
    }

    /**
     * Wraps function so its results are cached using the provided compute function.
     * @param function function to wrap
     * @param computeFunction cache compute function, like {@link java.util.Map#computeIfAbsent}
     * @param <T> type of input
     * @param <R> type of result
     * @return caching function
     */
    public static <T, R> @NotNull Function<T, R> caching(@NotNull Function<T, R> function, @NotNull BiFunction<T, Function<T, R>, R> computeFunction) {
        return new CFunction<>(function, computeFunction);
    }

    /**
     * Wraps supplier so it is computed only once.
     * @param supplier supplier to wrap
     * @param <R> type of result
     * @return caching supplier
     */
    public static <R> @NotNull Supplier<R> caching(@NotNull Supplier<R> supplier) {
        return new CSupplier<>(supplier);
    }

    private record CFunction<T, R>(
            @NotNull Function<T, R> origin,
            @NotNull BiFunction<T, Function<T, R>, R> computeFunction
    ) implements Function<T, R> {
        @Override
        public R apply(T t) {
            return computeFunction.apply(t, origin);
        }
    }

    private static final class CSupplier<R> implements Supplier<R> {
        private final Supplier<R> origin;
        private boolean cached;
        private R value;

        private CSupplier(@NotNull Supplier<R> origin) {
            this.origin = origin;
        }

        @Override
        public R get() {
            if (cached) return value;
            value = origin.get();
            cached = true;
            return value;
        }
    }
}
