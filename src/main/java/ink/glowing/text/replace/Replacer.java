package ink.glowing.text.replace;

import ink.glowing.text.Ink;
import ink.glowing.text.utils.GeneralUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.Component.text;

public interface Replacer extends Ink {
    @NotNull @Unmodifiable List<@NotNull FoundSpot> findSpots(@NotNull String str);

    static @NotNull Replacer replacer(@NotNull String search, @NotNull String replacement) {
        return replacer(search, text(replacement));
    }

    static @NotNull Replacer replacer(@NotNull String search, @NotNull Component replacement) {
        return replacer(search, () -> replacement);
    }

    static @NotNull Replacer replacer(@NotNull String search, @NotNull Supplier<Component> replacement) {
        return new Literal(search, replacement);
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
        return new Replacer.Regex(search, replacement);
    }

    record Literal(@NotNull String search, @NotNull Supplier<Component> replacement) implements Replacer {
        @Override
        public @NotNull List<FoundSpot> findSpots(@NotNull String text) {
            List<FoundSpot> spots = new ArrayList<>(0);
            GeneralUtils.findEach(text, search, (index) -> spots.add(new FoundSpot(index, search.length(), replacement)));
            return spots;
        }
    }

    record Regex(@NotNull Pattern pattern, @NotNull Function<MatchResult, Component> replacement) implements Replacer {
        @Override
        public @NotNull List<FoundSpot> findSpots(@NotNull String text) {
            Matcher matcher = pattern.matcher(text);
            if (!matcher.find()) return List.of();
            List<FoundSpot> spots = new ArrayList<>(0);
            do {
                MatchResult match = matcher.toMatchResult();
                spots.add(new FoundSpot(match.start(), match.end(), () -> replacement.apply(match)));
            } while (matcher.find());
            return spots;
        }
    }

    @ApiStatus.Internal
    record FoundSpot(int start, int end, Supplier<Component> replacement) implements Comparable<FoundSpot> {
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
