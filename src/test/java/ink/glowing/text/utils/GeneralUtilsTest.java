package ink.glowing.text.utils;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static ink.glowing.text.utils.GeneralUtils.*;
import static org.testng.Assert.assertEquals;

public class GeneralUtilsTest {
    @DataProvider
    public Object[][] concatData() {
        return new Object[][] {
                {new List[]{List.of("one"), List.of("two"), List.of("three")}, List.of("one", "two", "three")}
        };
    }

    @Test(dataProvider = "concatData")
    public void concatTest(List<String>[] lists, List<String> expected) {
        assertEquals(
                concat(ArrayList::new, lists),
                expected
        );
    }

    @DataProvider
    public Object[][] replaceEachData() {
        return new Object[][] {
                {"Foo bar one two three", "o", "a"}
        };
    }

    @Test(dataProvider = "replaceEachData")
    public void replaceEachTest(String input, String search, String replacement) { // TODO Some better replacement
        assertEquals(
                replaceEach(input, search, (i) -> replacement),
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
        int[] indexes = new int[5];
        findEach(input, search, i -> {
            indexes[count[0]++] = i;
        });
        assertEquals(
                indexes,
                expectedIndexes
        );
    }
}
