package ink.glowing.text.replace;

import ink.glowing.text.rich.RichNode;
import ink.glowing.text.utils.GeneralUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ink.glowing.text.rich.RichNode.literalNode;
import static ink.glowing.text.rich.RichNode.nodeId;

public sealed interface Replacer permits Replacer.Literal, Replacer.Regex {
    @Contract(mutates = "param2")
    @NotNull String replace(@NotNull String str, @NotNull List<RichNode> nodes);

    static @NotNull Replacer replacer(@NotNull String search, @NotNull String replacement) {
        return replacer(search, literalNode(replacement));
    }

    static @NotNull Replacer replacer(@NotNull String search, @NotNull RichNode replacement) {
        return replacer(search, () -> replacement);
    }

    static @NotNull Replacer replacer(@NotNull String search, @NotNull Supplier<RichNode> replacement) {
        return new Literal(search, replacement);
    }

    static @NotNull Replacer replacer(@NotNull Pattern search, @NotNull String replacement) {
        return replacer(search, literalNode(replacement));
    }

    static @NotNull Replacer replacer(@NotNull Pattern search, @NotNull RichNode replacement) {
        return replacer(search, (MatchResult match) -> replacement);
    }

    static @NotNull Replacer replacer(@NotNull Pattern search, @NotNull Supplier<RichNode> replacement) {
        return replacer(search, (MatchResult match) -> replacement.get());
    }

    static @NotNull Replacer replacer(@NotNull Pattern search, @NotNull Function<MatchResult, RichNode> replacement) {
        return new Replacer.Regex(search, replacement);
    }

    record Literal(@NotNull String search, @NotNull Supplier<RichNode> replacement) implements Replacer {
        @Override
        public @NotNull String replace(@NotNull String text, @NotNull List<RichNode> nodes) {
            return GeneralUtils.replaceEach(text, search, () -> {
                RichNode node = replacement.get();
                if (node == null) return search;
                nodes.add(node);
                return nodeId(nodes.size() - 1);
            });
        }
    }

    record Regex(@NotNull Pattern pattern, @NotNull Function<MatchResult, RichNode> replacement) implements Replacer {
        @Override
        public @NotNull String replace(@NotNull String text, @NotNull List<RichNode> nodes) {
            return pattern.matcher(text).replaceAll((match) -> {
                RichNode node = replacement.apply(match);
                if (node == null) return Matcher.quoteReplacement(match.group());
                nodes.add(node);
                return nodeId(nodes.size() - 1);
            });
        }
    }
}
