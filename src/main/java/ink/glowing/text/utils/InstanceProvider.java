package ink.glowing.text.utils;

import org.jetbrains.annotations.NotNull;

public interface InstanceProvider<T> {
    @NotNull T instance();
}
