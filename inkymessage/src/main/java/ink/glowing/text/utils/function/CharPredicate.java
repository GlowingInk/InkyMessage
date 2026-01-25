package ink.glowing.text.utils.function;

import ink.glowing.text.utils.TextUtils;
import org.jetbrains.annotations.NotNull;

import static ink.glowing.text.utils.TextUtils.subarray;

@FunctionalInterface
public interface CharPredicate {
    CharPredicate TRUE = _ -> true;
    CharPredicate FALSE = _ -> false;

    boolean test(char c);

    default @NotNull CharPredicate negate() {
        return c -> !test(c);
    }

    default @NotNull CharPredicate and(@NotNull CharPredicate other) {
        return c -> this.test(c) && other.test(c);
    }

    default @NotNull CharPredicate or(@NotNull CharPredicate other) {
        return c -> this.test(c) || other.test(c);
    }

    static @NotNull CharPredicate of(char ch) {
        return c -> c == ch;
    }

    static @NotNull CharPredicate anyOf(char ch1, char ch2) {
        return c -> c == ch1 || c == ch2;
    }

    static @NotNull CharPredicate anyOf(char ch1, char ch2, char ch3) {
        return c -> c == ch1 || c == ch2 || c == ch3;
    }

    static @NotNull CharPredicate anyOf(char ch1, char ch2, char ch3, char ch4) {
        return c -> c == ch1 || c == ch2 || c == ch3 || c == ch4;
    }

    static @NotNull CharPredicate anyOf(@NotNull CharSequence chars) {
        return anyOf(subarray(chars, 0));
    }

    static @NotNull CharPredicate anyOf(@NotNull String chars) {
        return anyOf(chars.toCharArray());
    }

    static @NotNull CharPredicate anyOf(char @NotNull ... chars) {
        return switch (chars.length) {
            case 0 -> FALSE;
            case 1 -> of(chars[0]);
            case 2 -> anyOf(chars[0], chars[1]);
            case 3 -> anyOf(chars[0], chars[1], chars[2]);
            case 4 -> anyOf(chars[0], chars[1], chars[2], chars[3]);
            default -> c -> TextUtils.indexOf(chars, c, 0, chars.length) != -1;
        };
    }
}
