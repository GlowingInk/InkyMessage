package ink.glowing.text.utils.function;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Caching { private Caching() {}
    public static <T, R> @NotNull Function<T, R> caching(@NotNull Function<T, R> function) {
        return new CFunction<>(function);
    }

    public static <R> @NotNull Supplier<R> caching(@NotNull Supplier<R> supplier) {
        return new CSupplier<>(supplier);
    }

    private static final class CFunction<T, R> implements Function<T, R> {
        private final Map<T, R> cache;
        private final Function<T, R> origin;

        private CFunction(Function<T, R> origin) {
            this.origin = origin;
            this.cache = new HashMap<>();
        }

        @Override
        public R apply(T t) {
            return cache.computeIfAbsent(t, origin);
        }
    }

    private static final class CSupplier<R> implements Supplier<R> {
        private final Supplier<R> origin;
        private R cache;

        private CSupplier(Supplier<R> origin) {
            this.origin = origin;
        }

        @Override
        public R get() {
            return cache == null
                    ? (cache = origin.get())
                    : cache;
        }
    }
}
