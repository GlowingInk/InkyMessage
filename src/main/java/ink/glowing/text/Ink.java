package ink.glowing.text;

import org.jetbrains.annotations.ApiStatus;

/**
 * An atom of style.
 */
public interface Ink {
    /**
     * A static instance of Ink, that can be safely stored.
     */
    @ApiStatus.NonExtendable
    interface Stable extends Ink { }
}
