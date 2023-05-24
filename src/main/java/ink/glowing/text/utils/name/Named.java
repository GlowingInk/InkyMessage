package ink.glowing.text.utils.name;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

public interface Named {
    @Language("RegExp")
    String NAME_PATTERN = "^[^\\sA-Z&(){}\\[\\]:\\\\]+$";

    @NamePattern @NotNull String name();
}
