package ink.glowing.text.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class GeneralUtils {
    private GeneralUtils() {}

    @SafeVarargs
    public static <T> @NotNull List<T> buildList(@NotNull Collection<T> @NotNull ... collections) {
        List<T> list = new ArrayList<>();
        for (var col : collections) list.addAll(col);
        return list;
    }

    @SafeVarargs
    public static <T> @NotNull @Unmodifiable List<T> buildImmutableList(@NotNull Collection<T> @NotNull ... collections) {
        return Collections.unmodifiableList(buildList(collections));
    }

    public static @NotNull String replaceEach(@NotNull String input, @NotNull String search, @NotNull Supplier<String> replaceSupplier) {
        int lastAppend = 0;
        StringBuilder builder = new StringBuilder();
        for (int index = input.indexOf(search, lastAppend); index != -1; index = input.indexOf(search, lastAppend)) {
            builder.append(input, lastAppend, index).append(replaceSupplier.get());
            lastAppend = index + search.length();
        }
        if (lastAppend != input.length()) {
            builder.append(input, lastAppend, input.length());
        }
        return builder.toString();
    }
}
