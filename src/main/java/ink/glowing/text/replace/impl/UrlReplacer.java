package ink.glowing.text.replace.impl;

import ink.glowing.text.replace.Replacer;
import ink.glowing.text.rich.RichNode;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.style.tag.impl.ClickTag;
import ink.glowing.text.utils.function.InstanceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

import static ink.glowing.text.rich.RichNode.literalNode;

public final class UrlReplacer implements Replacer.Regex {
    private static final Pattern SIMPLE_URL = Pattern.compile("[hH][tT]{2}[pP][sS]?://\\S+?\\.\\S+?(?:(?=[\\s)(\\]\\[.,!?])|\\S$)");

    public static @NotNull UrlReplacer urlReplacer() {
        return Provider.PROVIDER.instance;
    }

    @Override
    public @NotNull Pattern search() {
        return SIMPLE_URL;
    }

    @Override
    public @NotNull RichNode replace(@NotNull String found) {
        return literalNode(
                found,
                List.of(new StyleTag.Prepared(ClickTag.clickTag(), "url", found))
        );
    }

    private enum Provider implements InstanceProvider<UrlReplacer> {
        PROVIDER;
        private final UrlReplacer instance = new UrlReplacer();

        @Override
        public @NotNull UrlReplacer get() {
            return instance;
        }
    }
}
