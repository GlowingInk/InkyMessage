package ink.glowing.text.utils;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

public interface Named {
    String NAME_PATTERN = "^[^\\sA-Z&(){}\\[\\]:\\\\]+$";

    @Pattern(NAME_PATTERN) @NotNull String name();
}
