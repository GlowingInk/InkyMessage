package ink.glowing.text.rich.impl;

import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.rich.RichNode;
import ink.glowing.text.style.tag.StyleTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.Component.text;

@ApiStatus.Internal
public final class LiteralRichNode implements RichNode {
    private static final Pattern CHILD_NODE_PATTERN = Pattern.compile(RichNode.SECTION + "(\\d+)" + RichNode.SECTION);
    private final String text;
    private final List<StyleTag.Prepared> tags;

    public LiteralRichNode(@NotNull String text, @NotNull List<StyleTag.Prepared> tags) {
        this.text = text;
        this.tags = tags;
    }

    @Override
    public @NotNull Component render(@NotNull BuildContext context) {
        TextComponent.Builder builder = Component.text();
        Matcher matcher = CHILD_NODE_PATTERN.matcher(text);
        int lastAppend = 0;
        while (matcher.find()) {
            builder.append(text(text.substring(lastAppend, matcher.start())));
            builder.append(context.innerNode(Integer.parseInt(matcher.group(1))).render(context));
            lastAppend = matcher.end();
        }
        if (lastAppend != text.length()) {
            builder.append(text(text.substring(lastAppend)));
        }
        Component result = builder.build();
        for (var preparedTag : tags) {
            result = preparedTag.modify(result, context);
        }
        return result;
    }
}