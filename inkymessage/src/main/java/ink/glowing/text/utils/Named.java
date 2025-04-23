package ink.glowing.text.utils;

import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

public interface Named {
    @Language("RegExp")
    String NAME_PATTERN = "^[^\\sA-Z&(){}\\[\\]:\\\\]+$";

    @NamePattern @NotNull String name();

    @Documented
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
    @Pattern(Named.NAME_PATTERN)
    @interface NamePattern {}
}
