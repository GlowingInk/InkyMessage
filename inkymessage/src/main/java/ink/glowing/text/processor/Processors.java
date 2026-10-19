package ink.glowing.text.processor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Pair of functions applied to the input before and to the output after conversion.
 * @param <P> type of conversion input
 * @param <R> type of conversion result
 * @param <S> type of this processors
 */
public interface Processors<P, R, S extends Processors<P, R, S>> {
    /**
     * Creates new processors with the provided pre-processor.
     * @param preProcess function to apply to the input
     * @return the new processors
     */
    @Contract(value = "_ -> new", pure = true)
    @NotNull S preProcessor(@NotNull Function<P, P> preProcess);

    /**
     * Creates new processors with the provided post-processor.
     * @param postProcess function to apply to the result
     * @return the new processors
     */
    @Contract(value = "_ -> new", pure = true)
    @NotNull S postProcessor(@NotNull Function<R, R> postProcess);

    /**
     * Function applied to the input before conversion.
     * @return the pre-processor
     */
    @Contract(pure = true)
    @NotNull Function<P, P> preProcessor();

    /**
     * Function applied to the result after conversion.
     * @return the post-processor
     */
    @Contract(pure = true)
    @NotNull Function<R, R> postProcessor();

    /**
     * Applies the pre-processor to the input.
     * @param input conversion input
     * @return processed input
     */
    default @NotNull P preProcess(P input) {
        return preProcessor().apply(input);
    }

    /**
     * Applies the post-processor to the result.
     * @param output conversion result
     * @return processed result
     */
    default @NotNull R postProcess(R output) {
        return postProcessor().apply(output);
    }
}
