package ink.glowing.text.utils.processor;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface DecodeProcessors extends Processors<String, Component, DecodeProcessors> {
    @Contract(pure = true)
    static @NotNull DecodeProcessors identityDecodePreProcessors() {
        return DecodeProcessorsImpl.IDENTITY;
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull DecodeProcessors decodePreProcessor(@NotNull Function<String, String> preProcess) {
        return new DecodeProcessorsImpl(preProcess, Function.identity());
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull DecodeProcessors decodePostProcessor(@NotNull Function<Component, Component> postProcess) {
        return new DecodeProcessorsImpl(Function.identity(), postProcess);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull DecodeProcessors decodeProcessors(
            @NotNull Function<String, String> preProcess,
            @NotNull Function<Component, Component> postProcess
    ) {
        return new DecodeProcessorsImpl(preProcess, postProcess);
    }
}
