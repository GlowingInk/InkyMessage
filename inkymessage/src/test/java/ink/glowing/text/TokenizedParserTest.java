package ink.glowing.text;

import org.testng.annotations.Test;

import static ink.glowing.text.InkyMessage.inkyMessage;

public class TokenizedParserTest {
    private static final Context context = ((InkyMessageImpl) inkyMessage()).baseContext.stylelessCopy();

    @Test
    public void test() {
        String initial = "&[Fully clickable\\]](click:run /helloworld)";
        StringBuilder builder = new StringBuilder();
        for (var token : TokenizedParser.tokenize(initial, context.stylelessCopy())) {
            System.out.println(token.type() + "> " + token.text());
            builder.append(token.text());
        }
        System.out.println();
        System.out.println(initial);
        System.out.println(builder);
    }
}
