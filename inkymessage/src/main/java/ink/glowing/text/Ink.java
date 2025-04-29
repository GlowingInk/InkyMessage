package ink.glowing.text;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

/**
 * An atom of style.
 */
@ApiStatus.NonExtendable
public interface Ink {
    /**
     * Something that can provide Ink(s). Extendable.
     */
    @FunctionalInterface
    interface Provider extends Ink {
        static @NotNull Ink.Provider inkProvider(@NotNull Ink @NotNull ... inks) {
            return inkProvider(Arrays.asList(inks));
        }

        static @NotNull Ink.Provider inkProvider(@NotNull Iterable<? extends @NotNull Ink> inks) {
            return new InkProviderImpl(inks);
        }

        @SafeVarargs
        static @NotNull Ink.Provider composeInkProviders(@NotNull Iterable<? extends @NotNull Ink> @NotNull ... inkProviders) {
            return new InkProviderImpl(() -> Stream.of(inkProviders)
                    .<Ink>flatMap(provider -> stream(provider.spliterator(), false))
                    .iterator()
            );
        }

        static @NotNull Ink.Provider composeInkProviders(@NotNull Iterable<? extends Iterable<? extends Ink.@NotNull Provider>> inkProviders) {
            return new InkProviderImpl(() -> stream(inkProviders.spliterator(), false)
                    .<Ink>flatMap(provider -> stream(provider.spliterator(), false))
                    .iterator()
            );
        }

        @NotNull Iterable<? extends @NotNull Ink> inks();
    }
}
