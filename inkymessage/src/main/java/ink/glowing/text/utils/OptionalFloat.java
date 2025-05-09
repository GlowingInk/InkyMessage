package ink.glowing.text.utils;

import ink.glowing.text.utils.function.FloatConsumer;
import ink.glowing.text.utils.function.FloatSupplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public sealed interface OptionalFloat permits OptionalFloatImpl.PresentOptionalFloat, OptionalFloatImpl.EmptyOptionalFloat {
    boolean isPresent();

    float getAsFloat();

    void ifPresent(@NotNull FloatConsumer consumer);

    float orElse(float other);

    float orElseGet(@NotNull FloatSupplier supplier);

    <X extends Throwable> float orElseThrow(@NotNull Supplier<? extends X> exceptionSupplier) throws X;

    @Contract(value = "-> new", pure = true)
    @NotNull Optional<Float> toBoxed();

    static @NotNull OptionalFloat of(float value) {
        return new OptionalFloatImpl.PresentOptionalFloat(value);
    }

    @Contract("null -> fail")
    static @NotNull OptionalFloat of(Float value) {
        return new OptionalFloatImpl.PresentOptionalFloat(Objects.requireNonNull(value));
    }

    static @NotNull OptionalFloat ofNullable(@Nullable Float value) {
        return value == null ? empty() : of(value);
    }

    static @NotNull OptionalFloat empty() {
        return OptionalFloatImpl.EmptyOptionalFloat.INSTANCE;
    }
}