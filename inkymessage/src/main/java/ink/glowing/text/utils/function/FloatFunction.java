package ink.glowing.text.utils.function;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@FunctionalInterface
public interface FloatFunction<R> {
    R apply(float value);

    @Contract(value = "_ -> new", pure = true)
    default <V> Function<V, R> compose(@NotNull ToFloatFunction<? super V> before) {
        return (v) -> this.apply(before.applyAsFloat(v));
    }

    @Contract(value = "_ -> new", pure = true)
    default <V> FloatFunction<V> andThen(@NotNull Function<? super R, ? extends V> after) {
        return (t) -> after.apply(this.apply(t));
    }
}
