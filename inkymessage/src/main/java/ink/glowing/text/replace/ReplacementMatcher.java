package ink.glowing.text.replace;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.SequencedCollection;
import java.util.TreeSet;

public interface ReplacementMatcher {
    /**
     * Find replaceable spots in a string.
     * @param input string to replace in
     * @return found spots
     */
    @Contract(pure = true)
    @NotNull TreeSet<Replacer.FoundSpot> matchReplacements(@NotNull String input);

    static @NotNull ReplacementMatcher replacementMatcher(@NotNull Replacer @NotNull ... replacers) {
        return replacementMatcher(Arrays.asList(replacers));
    }

    static @NotNull ReplacementMatcher replacementMatcher(@NotNull SequencedCollection<? extends @NotNull Replacer> replacers) {
        return switch (replacers.size()) {
            case 0 -> (_) -> new TreeSet<>();
            case 1 -> replacers.getFirst();
            default -> replacementMatcher((Collection<? extends Replacer>) replacers);
        };
    }

    static @NotNull ReplacementMatcher replacementMatcher(@NotNull Iterable<? extends @NotNull Replacer> replacers) {
        return (input) -> {
            TreeSet<Replacer.FoundSpot> spots = new TreeSet<>();
            for (var replacer : replacers) {
                spots.addAll(replacer.findSpots(input));
            }
            return spots;
        };
    }

    static @NotNull ReplacementMatcher composeReplacementMatchers(@NotNull ReplacementMatcher @NotNull ... replacementMatchers) {
        return composeReplacementMatchers(Arrays.asList(replacementMatchers));
    }

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
