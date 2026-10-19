package ink.glowing.text.processor;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Processors applied during deserialization.
 */
public interface DecodeProcessors extends Processors<String, Component, DecodeProcessors> {
    /**
     * Processors that don't change anything.
     * @return identity processors
     */
    @Contract(pure = true)
    static @NotNull DecodeProcessors identityDecodeProcessors() {
        return DecodeProcessorsImpl.IDENTITY;
    }

    /**
     * Creates processors with only the pre-processor.
     * @param preProcess function to apply to the input string
     * @return the new processors
     */
    @Contract(value = "_ -> new", pure = true)
    static @NotNull DecodeProcessors decodePreProcessor(@NotNull Function<String, String> preProcess) {
        return new DecodeProcessorsImpl(preProcess, Function.identity());
    }

    /**
     * Creates processors with only the post-processor.
     * @param postProcess function to apply to the resulting component
     * @return the new processors
     */
    @Contract(value = "_ -> new", pure = true)
    static @NotNull DecodeProcessors decodePostProcessor(@NotNull Function<Component, Component> postProcess) {
        return new DecodeProcessorsImpl(Function.identity(), postProcess);
    }

    /**
     * Creates processors with both pre- and post-processor.
     * @param preProcess function to apply to the input string
     * @param postProcess function to apply to the resulting component
     * @return the new processors
     */
    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull DecodeProcessors decodeProcessors(
            @NotNull Function<String, String> preProcess,
            @NotNull Function<Component, Component> postProcess
    ) {
        return new DecodeProcessorsImpl(preProcess, postProcess);
    }
}
