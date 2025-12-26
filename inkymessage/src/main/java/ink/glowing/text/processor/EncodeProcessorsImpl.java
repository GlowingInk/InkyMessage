package ink.glowing.text.processor;


import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

record EncodeProcessorsImpl(
        Function<Component, Component> preProcessor,
        Function<String, String> postProcessor
) implements EncodeProcessors {
    public static final EncodeProcessors IDENTITY = new EncodeProcessorsImpl(Function.identity(), Function.identity());

    @Override
    public @NotNull EncodeProcessors preProcessor(@NotNull Function<Component, Component> newPreProcess) {
        return new EncodeProcessorsImpl(newPreProcess, postProcessor);
    }

    @Override
    public @NotNull EncodeProcessors postProcessor(@NotNull Function<String, String> newPostProcess) {
        return new EncodeProcessorsImpl(preProcessor, newPostProcess);
    }
}