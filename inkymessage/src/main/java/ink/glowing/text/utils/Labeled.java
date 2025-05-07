package ink.glowing.text.utils;

import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

public interface Labeled {
    @Language("RegExp")
    String LABEL_PATTERN = "^[^\\s&(){}\\[\\]<>:\\\\]+$";

    @LabelPattern @NotNull String label();

    @Documented
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
    @Pattern(Labeled.LABEL_PATTERN)
    @interface LabelPattern {}
}
