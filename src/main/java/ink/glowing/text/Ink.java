package ink.glowing.text;

import org.jetbrains.annotations.ApiStatus;

/**
 * An atom of style.
 */
@ApiStatus.NonExtendable
public interface Ink {
    /**
     * A static instance of Ink, that can be safely stored.
     */
    @ApiStatus.NonExtendable
    interface Stable extends Ink { } // TODO Better code split for performance
}
