package ink.glowing.text.replace;

import ink.glowing.text.Ink;
import ink.glowing.text.utils.function.IntObjectFunction;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.Component.text;

/**
 * Ink that replaces matching parts of the plain text with components.
 */
public non-sealed interface Replacer extends Ink, ReplacementMatcher {
    /**
     * Finds spots to replace in a string.
     * @param input string to replace in
     * @return found spots
     */
    @NotNull @Unmodifiable List<@NotNull FoundSpot> findSpots(@NotNull String input);

    @Override
    default @NotNull TreeSet<FoundSpot> matchReplacements(@NotNull String input) {
        return new TreeSet<>(findSpots(input));
    }

    /**
     * Creates a replacer of literal string with constant plain text.
     * @param search literal string to replace
     * @param replacement plain text to insert
     * @return the replacer
     */
    static @NotNull Replacer replacer(@NotNull String search, @NotNull String replacement) {
        return replacer(search, text(replacement));
    }

    /**
     * Creates a replacer of literal string with constant component.
     * @param search literal string to replace
     * @param replacement component to insert
     * @return the replacer
     */
    static @NotNull Replacer replacer(@NotNull String search, @NotNull Component replacement) {
        return replacerCounting(search, _ -> replacement);
    }

    /**
     * Creates a replacer of literal string with supplied component.
     * @param search literal string to replace
     * @param replacement supplier of component to insert
     * @return the replacer
     */
    static @NotNull Replacer replacer(@NotNull String search, @NotNull Supplier<Component> replacement) {
        return replacerCounting(search, _ -> replacement.get());
    }

    /**
     * Creates a replacer of literal string with component depending on the occurrence index.
     * @param search literal string to replace
     * @param replacement function converting zero-based occurrence index into component to insert
     * @return the replacer
     */
    static @NotNull Replacer replacerCounting(@NotNull String search, @NotNull IntFunction<Component> replacement) {
        return new LiteralReplacerImpl(search, replacement);
    }

    /**
     * Creates a replacer of regex pattern with constant plain text.
     * @param search pattern to replace
     * @param replacement plain text to insert
     * @return the replacer
     */
    static @NotNull Replacer replacer(@NotNull Pattern search, @NotNull String replacement) {
        return replacer(search, text(replacement));
    }

    /**
     * Creates a replacer of regex pattern with constant component.
     * @param search pattern to replace
     * @param replacement component to insert
     * @return the replacer
     */
    static @NotNull Replacer replacer(@NotNull Pattern search, @NotNull Component replacement) {
        return replacerCounting(search, (_, _) -> replacement);
    }

    /**
     * Creates a replacer of regex pattern with supplied component.
     * @param search pattern to replace
     * @param replacement supplier of component to insert
     * @return the replacer
     */
    static @NotNull Replacer replacer(@NotNull Pattern search, @NotNull Supplier<Component> replacement) {
        return replacerCounting(search, (_, _) -> replacement.get());
    }

    /**
     * Creates a replacer of regex pattern with component depending on the occurrence index.
     * @param search pattern to replace
     * @param replacement function converting zero-based occurrence index into component to insert
     * @return the replacer
     */
    static @NotNull Replacer replacerCounting(@NotNull Pattern search, @NotNull IntFunction<Component> replacement) {
        return replacerCounting(search, (i, _) -> replacement.apply(i));
    }

    /**
     * Creates a replacer of regex pattern with component depending on the match.
     * @param search pattern to replace
     * @param replacement function converting match into component to insert
     * @return the replacer
     */
    static @NotNull Replacer replacer(@NotNull Pattern search, @NotNull Function<MatchResult, Component> replacement) {
        return replacerCounting(search, (_, match) -> replacement.apply(match));
    }

    /**
     * Creates a replacer of regex pattern with component depending on the occurrence index and the match.
     * @param search pattern to replace
     * @param replacement function converting zero-based occurrence index and match into component to insert
     * @return the replacer
     */
    static @NotNull Replacer replacerCounting(@NotNull Pattern search, @NotNull IntObjectFunction<MatchResult, Component> replacement) {
        return new RegexReplacerImpl(search, replacement);
    }

    /**
     * Spot of the string to replace.
     * Spots are ordered by their start descending, and then by their end ascending,
     * so the longest spot is matched first when iterated in descending order.
     * @param start starting index (inclusive)
     * @param end ending index (exclusive)
     * @param count zero-based index of the occurrence
     * @param replacement function converting occurrence index into component to insert
     */
    record FoundSpot(int start, int end, int count, @NotNull IntFunction<Component> replacement) implements Comparable<FoundSpot> {
        @Override
        public int compareTo(@NotNull Replacer.FoundSpot o) {
            if (start == o.start) {
                return Integer.compare(end, o.end);
            } else if (start < o.start) {
                return 1;
            }
            return -1;
        }
    }
}
