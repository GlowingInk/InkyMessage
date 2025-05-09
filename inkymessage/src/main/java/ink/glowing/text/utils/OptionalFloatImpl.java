package ink.glowing.text.utils;

import ink.glowing.text.utils.function.FloatConsumer;
import ink.glowing.text.utils.function.FloatSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

final class OptionalFloatImpl {
    private OptionalFloatImpl() {}

    static final class PresentOptionalFloat implements OptionalFloat {
        private final float value;

        PresentOptionalFloat(float value) {
            this.value = value;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public float getAsFloat() {
            return value;
        }

        @Override
        public void ifPresent(@NotNull FloatConsumer consumer) {
            consumer.accept(value);
        }

        @Override
        public float orElse(float other) {
            return value;
        }

        @Override
        public float orElseGet(@NotNull FloatSupplier supplier) {
            return value;
        }

        @Override
        public <X extends Throwable> float orElseThrow(@NotNull Supplier<? extends X> exceptionSupplier) {
            return value;
        }

        @Override
        public @NotNull Optional<Float> toBoxed() {
            return Optional.of(value);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PresentOptionalFloat other &&
                    Float.compare(this.value, other.value) == 0;
        }

        @Override
        public int hashCode() {
            return Float.hashCode(value);
        }

        @Override
        public String toString() {
            return "OptionalFloat[" + value + "]";
        }
    }

    enum EmptyOptionalFloat implements OptionalFloat {
        INSTANCE;

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public float getAsFloat() {
            throw new NoSuchElementException("No value present");
        }

        @Override
        public void ifPresent(@NotNull FloatConsumer consumer) { }

        @Override
        public float orElse(float other) {
            return other;
        }

        @Override
        public float orElseGet(@NotNull FloatSupplier supplier) {
            return supplier.getAsFloat();
        }

        @Override
        public <X extends Throwable> float orElseThrow(@NotNull Supplier<? extends X> exceptionSupplier) throws X {
            throw exceptionSupplier.get();
        }

        @Override
        public @NotNull Optional<Float> toBoxed() {
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "OptionalFloat.empty";
        }
    }
}
