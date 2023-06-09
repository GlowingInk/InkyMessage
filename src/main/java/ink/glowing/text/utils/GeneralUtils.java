package ink.glowing.text.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public final class GeneralUtils {
    private GeneralUtils() {}

    @SafeVarargs
    public static <T, C extends Collection<T>> @NotNull C concat(
            @NotNull Supplier<C> colGetter,
            @NotNull Collection<? extends T> @NotNull ... collections
    ) {
        C col = colGetter.get();
        for (var iter : collections) col.addAll(iter);
        return col;
    }

    @SafeVarargs
    public static <T> @NotNull Collection<T> concatImmutable(
            @NotNull Supplier<Collection<T>> colGetter,
            @NotNull Collection<? extends T> @NotNull ... collections
    ) {
        return Collections.unmodifiableCollection(concat(colGetter, collections));
    }

    public static @NotNull String replaceEach(@NotNull String input, @NotNull String search, @NotNull IntFunction<String> replaceSupplier) {
        int lastAppend = 0;
        StringBuilder builder = new StringBuilder();
        for (int index = input.indexOf(search); index != -1; index = input.indexOf(search, lastAppend)) {
            builder.append(input, lastAppend, index).append(replaceSupplier.apply(index));
            lastAppend = index + search.length();
        }
        if (lastAppend != input.length()) {
            builder.append(input, lastAppend, input.length());
        }
        return builder.toString();
    }

    public static void findEach(@NotNull String input, @NotNull String search, @NotNull IntConsumer indexConsumer) {
        for (int index = input.indexOf(search); index != -1; index = input.indexOf(search, index + search.length())) {
            indexConsumer.accept(index);
        }
    }
}
