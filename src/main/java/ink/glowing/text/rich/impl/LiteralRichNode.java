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
public record LiteralRichNode(@NotNull String textStr, @NotNull List<StyleTag.Prepared> tags) implements RichNode {
    private static final Pattern CHILD_NODE_PATTERN = Pattern.compile(RichNode.SECTION + "(\\d+)" + RichNode.SECTION);
    
    @Override
    public @NotNull Component render(@NotNull BuildContext context) {
        TextComponent.Builder builder = Component.text();
        Matcher matcher = CHILD_NODE_PATTERN.matcher(textStr);
        int lastAppend = 0;
        while (matcher.find()) {
            builder.append(text(textStr.substring(lastAppend, matcher.start())));
            builder.append(context.innerNode(Integer.parseInt(matcher.group(1))).render(context));
            lastAppend = matcher.end();
        }
        if (lastAppend != textStr.length()) {
            builder.append(text(textStr.substring(lastAppend)));
        }
        Component result = builder.build();
        for (var preparedTag : tags) {
            result = preparedTag.modify(result, context);
        }
        return result;
    }
}
