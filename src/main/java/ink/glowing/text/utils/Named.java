package ink.glowing.text.utils;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

public interface Named {
    @Pattern("^[^\\sA-Z&(){}\\[\\]:\\\\]+$") @NotNull String name();
}
