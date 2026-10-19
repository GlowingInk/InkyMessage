package ink.glowing.text.replace;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.SequencedCollection;
import java.util.TreeSet;

/**
 * Matcher of the string spots to replace.
 */
public interface ReplacementMatcher {
    /**
     * Find replaceable spots in a string.
     * @param input string to replace in
     * @return found spots
     */
    @Contract(pure = true)
    @NotNull TreeSet<Replacer.FoundSpot> matchReplacements(@NotNull String input);

    /**
     * Creates a matcher of the provided replacers.
     * @param replacers replacers to match with
     * @return the matcher
     */
    static @NotNull ReplacementMatcher replacementMatcher(@NotNull Replacer @NotNull ... replacers) {
        return replacementMatcher(Arrays.asList(replacers));
    }

    /**
     * Creates a matcher of the provided replacers.
     * @param replacers replacers to match with
     * @return the matcher
     */
    static @NotNull ReplacementMatcher replacementMatcher(@NotNull SequencedCollection<? extends @NotNull Replacer> replacers) {
        return switch (replacers.size()) {
            case 0 -> (_) -> new TreeSet<>();
            case 1 -> replacers.getFirst();
            default -> replacementMatcher((Collection<? extends Replacer>) replacers);
        };
    }

    /**
     * Creates a matcher of the provided replacers.
     * The iterable is not copied, so its later changes are reflected by the matcher.
     * @param replacers replacers to match with
     * @return the matcher
     */
    static @NotNull ReplacementMatcher replacementMatcher(@NotNull Iterable<? extends @NotNull Replacer> replacers) {
        return (input) -> {
            TreeSet<Replacer.FoundSpot> spots = new TreeSet<>();
            for (var replacer : replacers) {
                spots.addAll(replacer.findSpots(input));
            }
            return spots;
        };
    }

    /**
     * Composes matchers into one, collecting spots of all of them.
     * @param replacementMatchers matchers to compose
     * @return the composed matcher
     */
    static @NotNull ReplacementMatcher composeReplacementMatchers(@NotNull ReplacementMatcher @NotNull ... replacementMatchers) {
        return composeReplacementMatchers(Arrays.asList(replacementMatchers));
    }

    /**
     * Composes matchers into one, collecting spots of all of them.
     * @param replacementMatchers matchers to compose
     * @return the composed matcher
     */
    static @NotNull ReplacementMatcher composeReplacementMatchers(@NotNull Iterable<? extends @NotNull ReplacementMatcher> replacementMatchers) {
        return (input) -> {
            TreeSet<Replacer.FoundSpot> spots = new TreeSet<>();
            for (var replacer : replacementMatchers) {
                spots.addAll(replacer.matchReplacements(input));
            }
            return spots;
        };
    }
}
