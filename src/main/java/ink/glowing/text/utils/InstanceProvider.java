package ink.glowing.text.utils;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface InstanceProvider<T> {
    @NotNull T instance();
}
