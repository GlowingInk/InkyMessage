package ink.glowing.text.modifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

final class ArgumentsImpl {
    private ArgumentsImpl() { }

    enum EmptyArguments implements Modifier.Arguments {
        INSTANCE;

        @Override
        public @NotNull String parameter() {
            return "";
        }

        @Override
        public @Unmodifiable @NotNull List<Modifier.@NotNull Argument> list() {
            return List.of();
        }

        @Override
        public @NotNull Modifier.Argument get(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
            return Modifier.Argument.emptyModifierArgument();
        }
    }

    record ParameterArguments(@NotNull String parameter) implements Modifier.Arguments {
        @Override
        public @Unmodifiable @NotNull List<Modifier.@NotNull Argument> list() {
            return List.of();
        }

        @Override
        public Modifier.@NotNull Argument get(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
            return ArgumentImpl.EmptyArgument.INSTANCE;
        }
    }

    record ListedArguments(@NotNull String parameter, @Unmodifiable @NotNull List<Modifier.@NotNull Argument> list) implements Modifier.Arguments {}
}
