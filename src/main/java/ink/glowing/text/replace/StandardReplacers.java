package ink.glowing.text.replace;

import ink.glowing.text.rich.RichNode;
import ink.glowing.text.style.tag.StyleTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static ink.glowing.text.replace.Replacer.replacer;
import static ink.glowing.text.rich.RichNode.literalNode;
import static ink.glowing.text.style.tag.standard.ClickTag.clickTag;

public final class StandardReplacers {
    private static final Replacer URL = replacer(
            Pattern.compile("[hH][tT]{2}[pP][sS]?://\\S+?\\.\\S+?(?:(?=[\\s()\\[\\].,!?])|\\S$)"),
            (Function<MatchResult, RichNode>) (match) -> {
                String group = match.group();
                return literalNode(
                        group,
                        List.of(new StyleTag.Prepared(clickTag(), "url", group))
                );
            }
    );

    public static @NotNull Replacer urlReplacer() {
        return URL;
    }
}
