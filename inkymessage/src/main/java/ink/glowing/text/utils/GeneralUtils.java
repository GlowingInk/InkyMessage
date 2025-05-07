package ink.glowing.text.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public final class GeneralUtils {
    private GeneralUtils() {}

    @SafeVarargs
    public static <T, C extends Collection<T>> @NotNull C concat(
            @NotNull Supplier<@NotNull C> colGetter,
            @NotNull Collection<? extends T> @NotNull ... collections
    ) {
        C col = colGetter.get();
        for (var elem : collections) col.addAll(elem);
        return col;
    }

    @SafeVarargs
    public static <T, C extends Collection<T>, R extends Collection<T>> @NotNull R concat(
            @NotNull Supplier<@NotNull C> colGetter,
            @NotNull Function<@NotNull C, @NotNull R> postProcess,
            @NotNull Collection<? extends T> @NotNull ... collections
    ) {
        C col = colGetter.get();
        for (var elem : collections) col.addAll(elem);
        return postProcess.apply(col);
    }

    public static @NotNull String replaceEach(
            @NotNull String input,
            @NotNull String search,
            @NotNull IntFunction<@NotNull String> replaceSupplier
    ) {
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

    public static void findEach(
            @NotNull String input,
            @NotNull String search,
            @NotNull IntConsumer indexConsumer
    ) {
        for (int index = input.indexOf(search); index != -1; index = input.indexOf(search, index + search.length())) {
            indexConsumer.accept(index);
        }
    }
}
