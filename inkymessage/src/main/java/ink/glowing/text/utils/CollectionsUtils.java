package ink.glowing.text.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility methods for working with collections.
 */
public final class CollectionsUtils {
    private CollectionsUtils() { }

    /**
     * Concatenates collections into a new one.
     * @param colGetter supplier of resulting collection
     * @param collections collections to concatenate
     * @param <T> type of elements
     * @param <C> type of resulting collection
     * @return collection containing elements of all collections
     */
    @SafeVarargs
    public static <T, C extends Collection<T>> @NotNull C concat(
            @NotNull Supplier<@NotNull C> colGetter,
            @NotNull Collection<? extends T> @NotNull ... collections
    ) {
        C col = colGetter.get();
        for (var elem : collections) col.addAll(elem);
        return col;
    }

    /**
     * Concatenates collections into a new one and post-processes it.
     * @param colGetter supplier of intermediate collection
     * @param postProcess function converting intermediate collection into the result
     * @param collections collections to concatenate
     * @param <T> type of elements
     * @param <C> type of intermediate collection
     * @param <R> type of resulting collection
     * @return post-processed collection containing elements of all collections
     */
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
