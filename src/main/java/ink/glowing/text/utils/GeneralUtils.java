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
    public static <T, C extends Collection<T>> @NotNull C concatCollection(
            @NotNull Supplier<C> colGetter,
            @NotNull Collection<T> @NotNull ... collections
    ) {
        C col = colGetter.get();
        for (var iter : collections) col.addAll(iter);
        return col;
    }

    @SafeVarargs
    public static <T> @NotNull Collection<T> concatImmutableCollection(
            @NotNull Supplier<Collection<T>> colGetter,
            @NotNull Collection<T> @NotNull ... collections
    ) {
        return Collections.unmodifiableCollection(concatCollection(colGetter, collections));
    }

    public static @NotNull String replaceEach(@NotNull String input, @NotNull String search, @NotNull IntFunction<String> replaceSupplier) {
        int lastAppend = 0;
        StringBuilder builder = new StringBuilder();
        for (int index = input.indexOf(search, lastAppend); index != -1; index = input.indexOf(search, lastAppend)) {
            builder.append(input, lastAppend, index).append(replaceSupplier.apply(index));
            lastAppend = index + search.length();
        }
        if (lastAppend != input.length()) {
            builder.append(input, lastAppend, input.length());
        }
        return builder.toString();
    }

    public static void findEach(@NotNull String input, @NotNull String search, @NotNull IntConsumer indexConsumer) {
        int lastFind = 0;
        for (int index = input.indexOf(search, lastFind); index != -1; index = input.indexOf(search, lastFind)) {
            indexConsumer.accept(index);
            lastFind = index + search.length();
        }
    }
}
