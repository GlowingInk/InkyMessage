package ink.glowing.text.utils;

import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * Something that can be identified by a label.
 */
public interface Labeled {
    /**
     * Regex pattern that every label should match.
     */
    @Language("RegExp")
    String LABEL_PATTERN = "^[^\\s&(){}\\[\\]<>:\\\\]+$";

    /**
     * Label of this object.
     * @return the label
     */
    @LabelPattern @NotNull String label();

    /**
     * Marks a string that should match {@link Labeled#LABEL_PATTERN}.
     */
    @Documented
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
    @Pattern(Labeled.LABEL_PATTERN)
    @interface LabelPattern { }
}
