package ink.glowing.text.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

/**
 * Utility methods for working with text and character arrays.
 */
public final class TextUtils {
    private TextUtils() { }

    /**
     * Replaces each occurrence of the search string with the result of the replacer.
     * @param input text to replace in
     * @param search string to search for
     * @param indexReplacer function providing replacement for occurrence at the given index
     * @return text with replacements
     */
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

    /**
     * Finds each non-overlapping occurrence of the search string.
     * @param input text to search in
     * @param search string to search for
     * @param indexConsumer consumer of each occurrence index
     */
    public static void findEach(
            @NotNull String input,
            @NotNull String search,
            @NotNull IntConsumer indexConsumer
    ) {
        for (int index = input.indexOf(search); index != -1; index = input.indexOf(search, index + search.length())) {
            indexConsumer.accept(index);
        }
    }

    /**
     * Finds the first index of character in array.
     * @param array array to search in
     * @param ch character to search for
     * @return index of character, or {@code -1} if not found
     */
    @Contract(pure = true)
    public static int indexOf(char @NotNull [] array, char ch) {
        return indexOf(array, ch, 0, array.length);
    }

    /**
     * Finds the first index of character in array.
     * @param array array to search in
     * @param ch character to search for
     * @param from starting index (inclusive)
     * @return index of character, or {@code -1} if not found
     */
    @Contract(pure = true)
    public static int indexOf(char @NotNull [] array, char ch, int from) {
        return indexOf(array, ch, from, array.length);
    }

    /**
     * Finds the first index of character in array.
     * @param array array to search in
     * @param ch character to search for
     * @param from starting index (inclusive)
     * @param to ending index (exclusive)
     * @return index of character, or {@code -1} if not found
     */
    @Contract(pure = true)
    public static int indexOf(char @NotNull [] array, char ch, int from, int to) {
        for (int i = from; i < to; i++) {
            if (array[i] == ch) return i;
        }
        return -1;
    }

    /**
     * Creates a string from part of character array.
     * @param src source character array
     * @param start starting index (inclusive)
     * @return the substring
     */
    @Contract(pure = true)
    public static @NotNull String substring(char @NotNull [] src, int start) {
        return new String(src, start, src.length - start);
    }

    /**
     * Creates a string from part of character array.
     * @param src source character array
     * @param start starting index (inclusive)
     * @param end ending index (exclusive)
     * @return the substring
     */
    @Contract(pure = true)
    public static @NotNull String substring(char @NotNull [] src, int start, int end) {
        return new String(src, start, end - start);
    }

    /**
     * Copies part of character array.
     * @param src source character array
     * @param start starting index (inclusive)
     * @return new character array
     */
    @Contract(pure = true)
    public static char @NotNull [] subarray(char @NotNull [] src, int start) {
        return subarray(src, start, src.length);
    }

    /**
     * Copies part of character array.
     * @param src source character array
     * @param start starting index (inclusive)
     * @param end ending index (exclusive)
     * @return new character array
     */
    @Contract(pure = true)
    public static char @NotNull [] subarray(char @NotNull [] src, int start, int end) {
        int len = end - start;
        char[] buf = new char[len];
        System.arraycopy(src, start, buf, 0, len);
        return buf;
    }

    /**
     * Copies part of character sequence into array.
     * @param src source character sequence
     * @param start starting index (inclusive)
     * @return new character array
     */
    @Contract(pure = true)
    public static char @NotNull [] subarray(@NotNull CharSequence src, int start) {
        return subarray(src, start, src.length());
    }

    /**
     * Copies part of character sequence into array.
     * @param src source character sequence
     * @param start starting index (inclusive)
     * @param end ending index (exclusive)
     * @return new character array
     */
    @Contract(pure = true)
    public static char @NotNull [] subarray(@NotNull CharSequence src, int start, int end) {
        char[] buf = new char[end - start];
        src.getChars(start, end, buf, 0);
        return buf;
    }
}
