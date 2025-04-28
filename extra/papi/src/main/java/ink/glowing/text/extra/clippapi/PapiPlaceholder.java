package ink.glowing.text.extra.clippapi;

import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.utils.Named.NamePattern;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentDecoder;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.empty;

public final class PapiPlaceholder {
    private PapiPlaceholder() { }

    public static @NotNull PapiPlaceholder.Builder builder() {
        return new PapiPlaceholder.Builder();
    }

    private record PapiPlaceholderImpl(
            @NotNull Function<String, ExpansionData> dataGetter,
            @NotNull String name,
            @Nullable OfflinePlayer player,
            @NotNull Function<String, Component> resultParser
    ) implements Placeholder {
        @Override
        public @NotNull Component parse(@NotNull String value) {
            var data = dataGetter.apply(value);
            if (data == null) return empty();
            String result = data.expansion.onRequest(player, data.params);
            return result != null ? resultParser.apply(result) : empty();
        }
    }

    public static final class Builder implements AbstractBuilder<Placeholder> {
        private static final Function<String, ExpansionData> DEFAULT_GETTER = (value) -> {
            String[] split = value.split("_", 2);
            PlaceholderExpansion expansion = getExpansion(split[0]);
            if (expansion == null) return null;
            String params = split.length > 1 ? split[1] : "";
            return new ExpansionData(expansion, params);
        };

        private Function<String, ExpansionData> dataGetter = DEFAULT_GETTER;
        private String prefix = "papi";
        private OfflinePlayer player = null;
        private Function<String, Component> resultParser = Component::text;

        @Contract(value = "_ -> new", pure = true)
        public @NotNull Builder prefix(@NotNull @NamePattern String prefix) {
            this.prefix = prefix;
            return this;
        }

        @Contract(value = "_ -> new", pure = true)
        public @NotNull Builder expansionName(@Nullable String expansionName) {
            if (expansionName == null) {
                this.dataGetter = DEFAULT_GETTER;
            } else {
                this.dataGetter = (value) -> {
                    var expansion = getExpansion(expansionName);
                    if (expansion == null) return null;
                    return new ExpansionData(expansion, value);
                };
            }
            return this;
        }

        @Contract(value = "_ -> new", pure = true)
        public @NotNull Builder player(@Nullable OfflinePlayer player) {
            this.player = player;
            return this;
        }

        @Contract(value = "_ -> new", pure = true)
        public @NotNull Builder resultParser(@NotNull Function<String, Component> resultParser) {
            this.resultParser = resultParser;
            return this;
        }

        @Contract(value = "_ -> new", pure = true)
        public @NotNull Builder resultParser(@NotNull ComponentDecoder<String, ? extends Component> resultParser) {
            this.resultParser = resultParser::deserialize;
            return this;
        }

        @Contract(value = "-> new", pure = true)
        public @NotNull Function<OfflinePlayer, Placeholder> asPlayerFunction() {
            var dataGetter = this.dataGetter;
            var name = this.prefix;
            var resultParser = this.resultParser;
            return (oPlayer) -> new PapiPlaceholderImpl(dataGetter, name, oPlayer, resultParser);
        }

        @Override
        @Contract(value = "-> new", pure = true)
        public @NotNull Placeholder build() {
            return new PapiPlaceholderImpl(dataGetter, prefix, player, resultParser);
        }
    }

    private record ExpansionData(@NotNull PlaceholderExpansion expansion, @NotNull String params) { }

    private static @Nullable PlaceholderExpansion getExpansion(@NotNull String name) {
        return PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().getExpansion(name.toLowerCase(Locale.ROOT));
    }
}
