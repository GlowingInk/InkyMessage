package ink.glowing.text.replace;

import ink.glowing.text.rich.RichNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

import static ink.glowing.text.rich.RichNode.node;

public sealed interface Replacer<T> permits Replacer.Literal, Replacer.Regex {
    @NotNull T search();

    @Nullable RichNode replace(@NotNull String found);

    static @NotNull Replacer.Literal literalReplacer(@NotNull String search, @NotNull String replacement) {
        return new Literal.Simple(search, replacement);
    }

    static @NotNull Replacer.Literal literalReplacer(@NotNull String search, @NotNull RichNode replacement) {
        return new Literal.Simple(search, replacement);
    }

    static @NotNull Replacer.Regex regexReplacer(@NotNull Pattern search, @NotNull String replacement) {
        return new Regex.Simple(search, replacement);
    }

    static @NotNull Replacer.Regex regexReplacer(@NotNull Pattern search, @NotNull RichNode replacement) {
        return new Regex.Simple(search, replacement);
    }

    non-sealed interface Literal extends Replacer<String> {
        final class Simple implements Literal {
            private final String search;
            private final RichNode replacement;

            private Simple(@NotNull String search, @NotNull String replacement) {
                this(search, node(replacement, List.of()));
            }

            private Simple(@NotNull String search, @NotNull RichNode replacement) {
                this.search = search;
                this.replacement = replacement;
            }

            @Override
            public @NotNull String search() {
                return search;
            }

            @Override
            public @NotNull RichNode replace(@NotNull String found) {
                return replacement;
            }
        }
    }
    
    non-sealed interface Regex extends Replacer<Pattern> {
        final class Simple implements Regex {
            private final Pattern search;
            private final RichNode replacement;

            private Simple(@NotNull Pattern search, @NotNull String replacement) {
                this(search, node(replacement, List.of()));
            }

            private Simple(@NotNull Pattern search, @NotNull RichNode replacement) {
                this.search = search;
                this.replacement = replacement;
            }

            @Override
            public @NotNull Pattern search() {
                return search;
            }

            @Override
            public @NotNull RichNode replace(@NotNull String found) {
                return replacement;
            }
        }
    }
}
