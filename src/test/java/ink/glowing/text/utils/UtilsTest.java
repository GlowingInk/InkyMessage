package ink.glowing.text.utils;

import ink.glowing.text.InkyMessage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class UtilsTest {
    @Test
    public void isEscapedTest() {
        assertEquals(
                InkyMessage.isEscaped("\\&[test]", 1),
                true
        );
    }
}