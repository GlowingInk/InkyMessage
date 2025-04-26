package ink.glowing.text;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
record InkProviderImpl(Iterable<? extends Ink> inks) implements Ink.Provider { }
