package ink.glowing.text.replace;

import ink.glowing.text.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

@ApiStatus.Internal
record LiteralReplacerImpl(@NotNull String search, @NotNull IntFunction<Component> replacement) implements Replacer {
    // TODO Test
    @Override
    public @NotNull List<FoundSpot> findSpots(@NotNull String input) {
        List<FoundSpot> spots = new ArrayList<>(0);
        int[] count = new int[1];
        TextUtils.findEach(
                input,
                search,
                (index) -> spots.add(new FoundSpot(index, index + search.length(), count[0]++, replacement))
        );
        return spots;
    }
}
