package ink.glowing.text.utils;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static ink.glowing.text.utils.CollectionsUtils.concat;
import static org.testng.Assert.assertEquals;

public class CollectionsUtilsTest {
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
}
