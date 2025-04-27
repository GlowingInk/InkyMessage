package ink.glowing.text;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
record InkProviderImpl(@NotNull Iterable<? extends @NotNull Ink> inks) implements Ink.Provider { }
