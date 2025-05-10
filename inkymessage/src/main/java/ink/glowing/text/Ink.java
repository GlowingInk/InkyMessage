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
     * The static creation methods don't create a new array/Collection.
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
        static @NotNull Ink.Provider inkProvider(@NotNull Iterable<? extends @NotNull Ink> @NotNull ... inks) {
            return new InkProviderImpl(() -> Stream.of(inks)
                    .<Ink>flatMap(inkCollection -> stream(inkCollection.spliterator(), false))
                    .iterator()
            );
        }

        @SafeVarargs
        static @NotNull Ink.Provider composeInkProviders(@NotNull Iterable<? extends Ink.@NotNull Provider> @NotNull ... inkProviders) {
            return composeInkProviders(Arrays.asList(inkProviders));
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
