package ink.glowing.text.replace;

import ink.glowing.text.utils.GeneralUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ApiStatus.Internal
record LiteralReplacerImpl(@NotNull String search, @NotNull Supplier<Component> replacement) implements Replacer {
    @Override
    public @NotNull List<FoundSpot> findSpots(@NotNull String input) {
        List<FoundSpot> spots = new ArrayList<>(0);
        GeneralUtils.findEach(input, search, (index) -> spots.add(new FoundSpot(index, search.length(), replacement)));
        return spots;
    }
}
