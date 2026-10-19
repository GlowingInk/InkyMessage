package ink.glowing.text.utils;

import ink.glowing.text.utils.function.FloatConsumer;
import ink.glowing.text.utils.function.FloatSupplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A container that may or may not contain a {@code float} value.
 */
public sealed interface OptionalFloat permits OptionalFloatImpl.PresentOptionalFloat, OptionalFloatImpl.EmptyOptionalFloat {
    /**
     * Checks if value is present.
     * @return is value present
     */
    boolean isPresent();

    /**
     * Returns the value if present, otherwise throws {@link java.util.NoSuchElementException}.
     * @return the value
     */
    float getAsFloat();

    /**
     * Performs the given action with the value if present.
     * @param consumer action to perform
     */
    void ifPresent(@NotNull FloatConsumer consumer);

    /**
     * Returns the value if present, otherwise returns {@code other}.
     * @param other value to return if value is not present
     * @return the value or {@code other}
     */
    float orElse(float other);

    /**
     * Returns the value if present, otherwise returns the result of {@code supplier}.
     * @param supplier supplier of value to return if value is not present
     * @return the value or the supplied value
     */
    float orElseGet(@NotNull FloatSupplier supplier);

    /**
     * Returns the value if present, otherwise throws an exception produced by {@code exceptionSupplier}.
     * @param exceptionSupplier supplier of exception to throw
     * @param <X> type of exception to throw
     * @return the value
     * @throws X if value is not present
     */
    <X extends Throwable> float orElseThrow(@NotNull Supplier<? extends X> exceptionSupplier) throws X;

    /**
     * Converts this optional into boxed {@link Optional}.
     * @return boxed optional
     */
    @Contract(value = "-> new", pure = true)
    @NotNull Optional<Float> toBoxed();

    /**
     * Creates an optional with the provided value.
     * @param value value to contain
     * @return optional with the value
     */
    @Contract(pure = true)
    static @NotNull OptionalFloat of(float value) {
        return new OptionalFloatImpl.PresentOptionalFloat(value);
    }

    /**
     * Creates an optional with the provided value.
     * @param value non-null value to contain
     * @return optional with the value
     * @throws NullPointerException if value is null
     */
    @Contract("null -> fail")
    static @NotNull OptionalFloat of(Float value) {
        return new OptionalFloatImpl.PresentOptionalFloat(Objects.requireNonNull(value));
    }

    /**
     * Creates an optional with the provided value, or an empty optional if value is null.
     * @param value value to contain
     * @return optional with the value or an empty optional
     */
    static @NotNull OptionalFloat ofNullable(@Nullable Float value) {
        return value == null
                ? empty()
                : of((float) value);
    }

    /**
     * Returns an empty optional.
     * @return empty optional
     */
    static @NotNull OptionalFloat empty() {
        return OptionalFloatImpl.EmptyOptionalFloat.INSTANCE;
    }
}