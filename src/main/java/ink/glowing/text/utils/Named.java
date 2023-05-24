package ink.glowing.text.utils;

import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

public interface Named {
    @Language("RegExp")
    String NAME_PATTERN = "^[^\\sA-Z&(){}\\[\\]:\\\\]+$";

    @Pattern(Named.NAME_PATTERN) @NotNull String name();
}
