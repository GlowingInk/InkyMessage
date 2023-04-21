package ink.glowing.text.utils;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@FunctionalInterface
public interface InstanceProvider<T> extends Supplier<T> {
    @NotNull T get();
}
