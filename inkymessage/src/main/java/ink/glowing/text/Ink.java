package ink.glowing.text;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * An atom of style.
 */
@ApiStatus.NonExtendable
public interface Ink {
    static @NotNull Ink.Provider inkProvider(@NotNull Ink @NotNull ... inks) {
        return inkProvider(Arrays.asList(inks));
    }

    static @NotNull Ink.Provider inkProvider(@NotNull Iterable<? extends @NotNull Ink> inks) {
        return new InkProviderImpl(inks);
    }

    /**
     * Something that can supply with Ink(s). Extendable.
     */
    interface Provider extends Ink {
        @NotNull Iterable<? extends @NotNull Ink> inks();
    }
}
