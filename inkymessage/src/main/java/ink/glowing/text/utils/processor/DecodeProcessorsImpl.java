package ink.glowing.text.utils.processor;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

record DecodeProcessorsImpl(
        @NotNull Function<String, String> preProcessor,
        @NotNull Function<Component, Component> postProcessor
) implements DecodeProcessors {
    public static final DecodeProcessors IDENTITY = new DecodeProcessorsImpl(Function.identity(), Function.identity());

    @Override
    public @NotNull DecodeProcessors preProcessor(@NotNull Function<String, String> newPreProcess) {
        return new DecodeProcessorsImpl(newPreProcess, postProcessor);
    }

    @Override
    public @NotNull DecodeProcessors postProcessor(@NotNull Function<Component, Component> newPostProcess) {
        return new DecodeProcessorsImpl(preProcessor, newPostProcess);
    }
}