package ink.glowing.text.utils.function;

/**
 * Supplier of {@code float} values.
 */
@FunctionalInterface
public interface FloatSupplier {
    /**
     * Gets a result.
     * @return the result
     */
    float getAsFloat();
}
