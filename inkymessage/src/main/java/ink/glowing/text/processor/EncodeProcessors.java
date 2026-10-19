package ink.glowing.text.processor;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Processors applied during serialization.
 */
public interface EncodeProcessors extends Processors<Component, String, EncodeProcessors>  {
    /**
     * Processors that don't change anything.
     * @return identity processors
     */
    static @NotNull EncodeProcessors identityEncodePreprocessors() {
        return EncodeProcessorsImpl.IDENTITY;
    }

    /**
     * Creates processors with only the pre-processor.
     * @param preProcess function to apply to the input component
     * @return the new processors
     */
    @Contract(value = "_ -> new", pure = true)
    static @NotNull EncodeProcessors encodePreProcessor(@NotNull Function<Component, Component> preProcess) {
        return new EncodeProcessorsImpl(preProcess, Function.identity());
    }

    /**
     * Creates processors with only the post-processor.
     * @param postProcess function to apply to the resulting string
     * @return the new processors
     */
    @Contract(value = "_ -> new", pure = true)
    static @NotNull EncodeProcessors encodePostProcessor(@NotNull Function<String, String> postProcess) {
        return new EncodeProcessorsImpl(Function.identity(), postProcess);
    }

    /**
     * Creates processors with both pre- and post-processor.
     * @param preProcess function to apply to the input component
     * @param postProcess function to apply to the resulting string
     * @return the new processors
     */
    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull EncodeProcessors encodeProcessors(
            @NotNull Function<Component, Component> preProcess,
            @NotNull Function<String, String> postProcess
    ) {
        return new EncodeProcessorsImpl(preProcess, postProcess);
    }
}
