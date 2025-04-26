package ink.glowing.text.replace;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record RegexReplacer(@NotNull Pattern pattern,
                            @NotNull Function<MatchResult, Component> replacement) implements Replacer {
    @Override
    public @NotNull List<FoundSpot> findSpots(@NotNull String input) {
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) return List.of();
        List<FoundSpot> spots = new ArrayList<>(0);
        do {
            MatchResult match = matcher.toMatchResult();
            spots.add(new FoundSpot(match.start(), match.end(), () -> replacement.apply(match)));
        } while (matcher.find());
        return spots;
    }
}
