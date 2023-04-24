package ink.glowing.text.utils;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static ink.glowing.text.utils.GeneralUtils.buildList;
import static ink.glowing.text.utils.GeneralUtils.replaceEach;
import static org.testng.Assert.assertEquals;

public class GeneralUtilsTest {
    @DataProvider
    public Object[][] buildListData() {
        return new Object[][] {
                {new List[]{List.of("one"), List.of("two"), List.of("three")}, List.of("one", "two", "three")}
        };
    }

    @Test(dataProvider = "buildListData")
    public void buildListTest(List<String>[] lists, List<String> expected) {
        assertEquals(
                buildList(lists),
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
                replaceEach(input, search, () -> replacement),
                input.replace(search, replacement)
        );
    }
}
