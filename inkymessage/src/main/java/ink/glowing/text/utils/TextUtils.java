package ink.glowing.text.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public final class TextUtils {
    private TextUtils() {}

    public static @NotNull String replaceEach(
            @NotNull String input,
            @NotNull String search,
            @NotNull IntFunction<@NotNull String> indexReplacer
    ) {
        int lastAppend = 0;
        StringBuilder builder = new StringBuilder();
        for (int index = input.indexOf(search); index != -1; index = input.indexOf(search, lastAppend)) {
            builder.append(input, lastAppend, index).append(indexReplacer.apply(index));
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

    @Contract(pure = true)
    public static int indexOf(char @NotNull [] array, char ch) {
        return indexOf(array, ch, 0, array.length);
    }

    @Contract(pure = true)
    public static int indexOf(char @NotNull [] array, char ch, int from) {
        return indexOf(array, ch, from, array.length);
    }

    @Contract(pure = true)
    public static int indexOf(char @NotNull [] array, char ch, int from, int to) {
        for (int i = from; i < to; i++) {
            if (array[i] == ch) return i;
        }
        return -1;
    }

    @Contract(pure = true)
    public static @NotNull String substring(char @NotNull [] src, int start) {
        return new String(src, start, src.length - start);
    }

    @Contract(pure = true)
    public static @NotNull String substring(char @NotNull [] src, int start, int end) {
        return new String(src, start, end - start);
    }

    @Contract(pure = true)
    public static char @NotNull [] subarray(char @NotNull [] src, int start) {
        return subarray(src, start, src.length);
    }

    @Contract(pure = true)
    public static char @NotNull [] subarray(char @NotNull [] src, int start, int end) {
        int len = end - start;
        char[] buf = new char[len];
        System.arraycopy(src, start, buf, 0, len);
        return buf;
    }

    @Contract(pure = true)
    public static char @NotNull [] subarray(@NotNull CharSequence src, int start) {
        return subarray(src, start, src.length());
    }

    @Contract(pure = true)
    public static char @NotNull [] subarray(@NotNull CharSequence src, int start, int end) {
        char[] buf = new char[end - start];
        src.getChars(start, end, buf, 0);
        return buf;
    }
}
