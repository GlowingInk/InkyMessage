package ink.glowing.text.utils.function;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Caching {
    private Caching() {}

    public static <T, R> @NotNull Function<T, R> caching(@NotNull Function<T, R> function) {
        return new CFunction<>(function, new HashMap<T, R>()::computeIfAbsent);
    }

    public static <T, R> @NotNull Function<T, R> caching(@NotNull Function<T, R> function, @NotNull BiFunction<T, Function<T, R>, R> computeFunction) {
        return new CFunction<>(function, computeFunction);
    }

    public static <R> @NotNull Supplier<R> caching(@NotNull Supplier<R> supplier) {
        return new CSupplier<>(supplier);
    }

    private static final class CFunction<T, R> implements Function<T, R> {
        private final Function<T, R> origin;
        private final BiFunction<T, Function<T, R>, R> computeFunction;

        private CFunction(@NotNull Function<T, R> origin, @NotNull BiFunction<T, Function<T, R>, R> computeFunction) {
            this.origin = origin;
            this.computeFunction = computeFunction;
        }

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
