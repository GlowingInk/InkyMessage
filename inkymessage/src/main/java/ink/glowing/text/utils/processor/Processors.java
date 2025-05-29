package ink.glowing.text.utils.processor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface Processors<P, R, S extends Processors<P, R, S>> {
    @Contract(value = "_ -> new", pure = true)
    @NotNull S preProcessor(@NotNull Function<P, P> preProcess);

    @Contract(value = "_ -> new", pure = true)
    @NotNull S postProcessor(@NotNull Function<R, R> postProcess);

    @Contract(pure = true)
    @NotNull Function<P, P> preProcessor();

    @Contract(pure = true)
    @NotNull Function<R, R> postProcessor();

    default @NotNull P preProcess(P input) {
        return preProcessor().apply(input);
    }

    default @NotNull R postProcess(R output) {
        return postProcessor().apply(output);
    }
}
