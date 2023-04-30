package ink.glowing.text.utils.function;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface InstanceProvider<T> {
    @NotNull T instance();
}
