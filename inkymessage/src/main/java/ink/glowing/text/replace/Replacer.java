package ink.glowing.text.replace;

import ink.glowing.text.Ink;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.Component.text;

public interface Replacer extends Ink, ReplacementMatcher {
    @NotNull @Unmodifiable List<@NotNull FoundSpot> findSpots(@NotNull String input);

    @Override
    default @NotNull TreeSet<FoundSpot> matchReplacements(@NotNull String input) {
        return new TreeSet<>(findSpots(input));
    }

    static @NotNull Replacer replacer(@NotNull String search, @NotNull String replacement) {
        return replacer(search, text(replacement));
    }

    static @NotNull Replacer replacer(@NotNull String search, @NotNull Component replacement) {
        return replacer(search, () -> replacement);
    }

    static @NotNull Replacer replacer(@NotNull String search, @NotNull Supplier<Component> replacement) {
        return new LiteralReplacerImpl(search, replacement);
    }

    static @NotNull Replacer replacer(@NotNull Pattern search, @NotNull String replacement) {
        return replacer(search, text(replacement));
    }

    static @NotNull Replacer replacer(@NotNull Pattern search, @NotNull Component replacement) {
        return replacer(search, (MatchResult match) -> replacement);
    }

    static @NotNull Replacer replacer(@NotNull Pattern search, @NotNull Supplier<Component> replacement) {
        return replacer(search, (MatchResult match) -> replacement.get());
    }

    static @NotNull Replacer replacer(@NotNull Pattern search, @NotNull Function<MatchResult, Component> replacement) {
        return new RegexReplacerImpl(search, replacement);
    }

    record FoundSpot(int start, int end, @NotNull Supplier<Component> replacement) implements Comparable<FoundSpot> {
        @Override
        public int compareTo(@NotNull Replacer.FoundSpot o) {
            if (start == o.start) {
                return Integer.compare(o.end, end);
            } else if (start < o.start) {
                return 1;
            }
            return -1;
        }
    }
}
