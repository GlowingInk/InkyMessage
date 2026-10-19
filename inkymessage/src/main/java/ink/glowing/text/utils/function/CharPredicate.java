package ink.glowing.text.utils.function;

import ink.glowing.text.utils.TextUtils;
import org.jetbrains.annotations.NotNull;

import static ink.glowing.text.utils.TextUtils.subarray;

/**
 * Predicate of one {@code char} argument.
 */
@FunctionalInterface
public interface CharPredicate {
    /**
     * Predicate that always returns {@code true}.
     */
    CharPredicate TRUE = _ -> true;
    /**
     * Predicate that always returns {@code false}.
     */
    CharPredicate FALSE = _ -> false;

    /**
     * Evaluates this predicate on the given character.
     * @param c character to test
     * @return does character match the predicate
     */
    boolean test(char c);

    /**
     * Returns a predicate that is the negation of this predicate.
     * @return negated predicate
     */
    default @NotNull CharPredicate negate() {
        return c -> !test(c);
    }

    /**
     * Returns a predicate that is a logical AND of this and other predicate.
     * @param other other predicate
     * @return combined predicate
     */
    default @NotNull CharPredicate and(@NotNull CharPredicate other) {
        return c -> this.test(c) && other.test(c);
    }

    /**
     * Returns a predicate that is a logical OR of this and other predicate.
     * @param other other predicate
     * @return combined predicate
     */
    default @NotNull CharPredicate or(@NotNull CharPredicate other) {
        return c -> this.test(c) || other.test(c);
    }

    /**
     * Creates a predicate matching the provided character.
     * @param ch character to match
     * @return the predicate
     */
    static @NotNull CharPredicate of(char ch) {
        return c -> c == ch;
    }

    /**
     * Creates a predicate matching any of the provided characters.
     * @param ch1 first character to match
     * @param ch2 second character to match
     * @return the predicate
     */
    static @NotNull CharPredicate anyOf(char ch1, char ch2) {
        return c -> c == ch1 || c == ch2;
    }

    /**
     * Creates a predicate matching any of the provided characters.
     * @param ch1 first character to match
     * @param ch2 second character to match
     * @param ch3 third character to match
     * @return the predicate
     */
    static @NotNull CharPredicate anyOf(char ch1, char ch2, char ch3) {
        return c -> c == ch1 || c == ch2 || c == ch3;
    }

    /**
     * Creates a predicate matching any of the provided characters.
     * @param ch1 first character to match
     * @param ch2 second character to match
     * @param ch3 third character to match
     * @param ch4 fourth character to match
     * @return the predicate
     */
    static @NotNull CharPredicate anyOf(char ch1, char ch2, char ch3, char ch4) {
        return c -> c == ch1 || c == ch2 || c == ch3 || c == ch4;
    }

    /**
     * Creates a predicate matching any of the provided characters.
     * @param chars characters to match
     * @return the predicate
     */
    static @NotNull CharPredicate anyOf(@NotNull CharSequence chars) {
        return anyOf(subarray(chars, 0));
    }

    /**
     * Creates a predicate matching any of the provided characters.
     * @param chars characters to match
     * @return the predicate
     */
    static @NotNull CharPredicate anyOf(@NotNull String chars) {
        return anyOf(chars.toCharArray());
    }

    /**
     * Creates a predicate matching any of the provided characters.
     * @param chars characters to match
     * @return the predicate
     */
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
