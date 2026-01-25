package ink.glowing.text.utils;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static ink.glowing.text.utils.TextUtils.*;
import static org.testng.Assert.assertEquals;

public class TextUtilsTest {
    @DataProvider
    public Object[][] replaceEachData() {
        return new Object[][] {
                {"Foo bar one two three", "o", "a"}
        };
    }

    @Test(dataProvider = "replaceEachData")
    public void replaceEachTest(String input, String search, String replacement) { // TODO Some better replacement
        assertEquals(
                TextUtils.replaceEach(input, search, (_) -> replacement),
                input.replace(search, replacement)
        );
    }

    @DataProvider
    public Object[][] findEachData() {
        return new Object[][] {
                {"1a 2a 3a 4a 5a", "a", new int[]{1, 4, 7, 10, 13}}
        };
    }

    @Test(dataProvider = "findEachData")
    public void findEachTest(String input, String search, int[] expectedIndexes) { // TODO Some better replacement
        final int[] count = {0};
        int[] indexes = new int[expectedIndexes.length];
        TextUtils.findEach(input, search, i -> indexes[count[0]++] = i);
        assertEquals(
                indexes,
                expectedIndexes
        );
    }

    @DataProvider
    public Object[][] indexOfData() {
        return new Object[][]{
                {"abcd", 'b', 0, 4, 1},
                {"abcd", 'b', 2, 4, -1},
                {"abbc", 'b', 0, 4, 1},
                {"abbc", 'b', 2, 4, 2},
                {"abcd", 'e', 0, 4, -1},
                {"abcd", 'a', 0, 4, 0},
                {"abcd", 'd', 0, 4, 3},
                {"abcd", 'd', 0, 3, -1},
                {"aaaa", 'a', 0, 4, 0},
                {"aaaa", 'a', 2, 4, 2},
                {"", 'a', 0, 0, -1},
                {"a", 'a', 0, 1, 0},
                {"a", 'b', 0, 1, -1}
        };
    }

    @Test(dataProvider = "indexOfData")
    public void indexOfTest(String arrayStr, char ch, int from, int to, int expected) {
        assertEquals(
                indexOf(arrayStr.toCharArray(), ch, from, to),
                expected
        );
    }

    @DataProvider
    public Object[][] substringData() {
        return new Object[][]{
                {"Hello", 0, 5, "Hello"},
                {"Hello", 1, 4, "ell"},
                {"Hello", 1, 5, "ello"},
                {"Hello", 0, 0, ""},
                {"Hello", 2, 2, ""},
                {"Hello", 4, 5, "o"},
                {"abc", 0, 3, "abc"},
                {"abc", 1, 2, "b"},
                {" ", 0, 1, " "},
                {"123", 0, 1, "1"}
        };
    }

    @Test(dataProvider = "substringData")
    public void substringTest(String srcStr, int start, int end, String expected) {
        assertEquals(
                substring(srcStr.toCharArray(), start, end),
                expected
        );
    }

    @DataProvider
    public Object[][] subarray_CharArrayData() {
        return new Object[][]{
                {"abcde", 0, 5, "abcde"},
                {"abcde", 1, 4, "bcd"},
                {"abcde", 2, 5, "cde"},
                {"abcde", 0, 3, "abc"},
                {"abcde", 0, 0, ""},
                {"abcde", 4, 5, "e"},
                {"abc", 0, 3, "abc"},
                {"abc", 1, 2, "b"},
                {"x", 0, 1, "x"},
                {"123", 0, 1, "1"}
        };
    }

    @Test(dataProvider = "subarray_CharArrayData")
    public void subarray_CharArrayTest(String srcStr, int start, int end, String expected) {
        assertEquals(
                subarray(srcStr.toCharArray(), start, end),
                expected.toCharArray()
        );
    }

    @DataProvider
    public Object[][] subarray_CharSequenceData() {
        return new Object[][]{
                {"Hello", 0, 5, "Hello"},
                {"Hello", 1, 4, "ell"},
                {"Hello", 1, 5, "ello"},
                {"Hello", 0, 0, ""},
                {"Hello", 2, 2, ""},
                {"Hello", 4, 5, "o"},
                {"abc", 0, 3, "abc"},
                {"abc", 1, 2, "b"},
                {" ", 0, 1, " "},
                {"123", 0, 1, "1"},
                {"", 0, 0, ""},
                {"test", 2, 4, "st"},
                {"test", 0, 1, "t"}
        };
    }

    @Test(dataProvider = "subarray_CharSequenceData")
    public void subarray_CharSequenceTest(CharSequence src, int start, int end, String expected) {
        assertEquals(
                subarray(src, start, end),
                expected.toCharArray()
        );
    }
}
