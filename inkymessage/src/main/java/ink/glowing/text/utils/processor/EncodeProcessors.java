package ink.glowing.text.utils.processor;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface EncodeProcessors extends Processors<Component, String, EncodeProcessors>  {
    static @NotNull EncodeProcessors identityEncodePreprocessors() {
        return EncodeProcessorsImpl.IDENTITY;
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull EncodeProcessors encodePreProcessor(@NotNull Function<Component, Component> preProcess) {
        return new EncodeProcessorsImpl(preProcess, Function.identity());
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull EncodeProcessors encodePostProcessor(@NotNull Function<String, String> postProcess) {
        return new EncodeProcessorsImpl(Function.identity(), postProcess);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull EncodeProcessors encodeProcessors(
            @NotNull Function<Component, Component> preProcess,
            @NotNull Function<String, String> postProcess
    ) {
        return new EncodeProcessorsImpl(preProcess, postProcess);
    }
}
