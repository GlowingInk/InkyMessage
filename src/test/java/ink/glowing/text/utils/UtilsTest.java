package ink.glowing.text.utils;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class UtilsTest {
    @Test
    public void isEscapedTest() {
        assertEquals(
                Utils.isEscaped("\\&[test]", 1),
                true
        );
    }
}