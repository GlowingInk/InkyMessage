package ink.glowing.text.extra.clippapi;

import ink.glowing.text.Context;
import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.utils.Labeled.LabelPattern;
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

/**
 * Placeholders based on PlaceholderAPI expansions.
 */
public final class PapiPlaceholder {
    private PapiPlaceholder() { }

    /**
     * Creates a new PlaceholderAPI placeholder builder.
     * @return a builder
     */
    public static @NotNull PapiPlaceholder.Builder builder() {
        return new PapiPlaceholder.Builder();
    }

    private record PapiPlaceholderImpl(
            @NotNull Function<String, ExpansionData> dataGetter,
            @NotNull String label,
            @Nullable OfflinePlayer player,
            @NotNull Function<String, Component> resultParser
    ) implements Placeholder {
        @Override
        public @NotNull Component retrieve(@NotNull String value, @NotNull Context context) {
            var data = dataGetter.apply(value);
            if (data == null) return empty();
            String result = data.expansion.onRequest(player, data.params);
            return result != null ? resultParser.apply(result) : empty();
        }
    }

    /**
     * Builder of PlaceholderAPI placeholders.
     * By default, placeholder is labeled {@code papi}, has no player, takes expansion name from the parameter
     * (e.g. {@code &{papi:player_name}}), and inserts the result as plain text.
     */
    public static final class Builder implements AbstractBuilder<Placeholder> {
        private static final Function<String, ExpansionData> DEFAULT_GETTER = (value) -> {
            String[] split = value.split("_", 2);
            PlaceholderExpansion expansion = getExpansion(split[0]);
            if (expansion == null) return null;
            String params = split.length > 1 ? split[1] : "";
            return new ExpansionData(expansion, params);
        };

        private Function<String, ExpansionData> dataGetter;
        private String prefix;
        private OfflinePlayer player;
        private Function<String, Component> resultParser;

        private Builder() {
            this.dataGetter = DEFAULT_GETTER;
            this.prefix = "papi";
            this.player = null;
            this.resultParser = Component::text;
        }

        /**
         * Sets label of placeholder.
         * @param prefix label of placeholder
         * @return this builder
         */
        @Contract("_ -> this")
        public @NotNull Builder prefix(@NotNull @LabelPattern String prefix) {
            this.prefix = prefix;
            return this;
        }

        /**
         * Sets expansion to use for every request, so the whole parameter is passed to it.
         * @param expansionName name of expansion, or {@code null} to take it from the parameter before the first {@code _}
         * @return this builder
         */
        @Contract("_ -> this")
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

        /**
         * Sets player to request placeholders for.
         * @param player player to request for, or {@code null} for none
         * @return this builder
         */
        @Contract("_ -> this")
        public @NotNull Builder player(@Nullable OfflinePlayer player) {
            this.player = player;
            return this;
        }

        /**
         * Sets function converting the expansion result into component.
         * @param resultParser result converting function
         * @return this builder
         */
        @Contract("_ -> this")
        public @NotNull Builder resultParser(@NotNull Function<String, Component> resultParser) {
            this.resultParser = resultParser;
            return this;
        }

        /**
         * Sets decoder converting the expansion result into component.
         * @param resultParser result decoder
         * @return this builder
         */
        @Contract("_ -> this")
        public @NotNull Builder resultParser(@NotNull ComponentDecoder<String, ? extends Component> resultParser) {
            this.resultParser = resultParser::deserialize;
            return this;
        }

        /**
         * Creates a function building placeholders for the provided player with current settings.
         * The configured player is ignored.
         * @return function creating placeholders for players
         */
        @Contract(value = "-> new", pure = true)
        public @NotNull Function<OfflinePlayer, Placeholder> asPlayerFunction() {
            var dataGetter = this.dataGetter;
            var label = this.prefix;
            var resultParser = this.resultParser;
            return (oPlayer) -> new PapiPlaceholderImpl(dataGetter, label, oPlayer, resultParser);
        }

        /**
         * Builds new placeholder.
         * @return the new placeholder
         */
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
