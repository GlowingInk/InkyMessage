package ink.glowing.text.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CollectionsUtils {
    private CollectionsUtils() {}

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
}
