package de.cheaterpaul.fallingleaves.modcompat;

import com.google.common.collect.Lists;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SereneSeasonsConfig {

    private final ForgeConfigSpec.ConfigValue<List<? extends String>> seasonFallRate;

    private Map<Enum<?>, Float> enabledSeasons = new HashMap<>();

    public SereneSeasonsConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Serene Seasons");

        seasonFallRate = builder.comment("spawnrate modifier per season/subseason", "Format: '<season>:<modifier>'; eg 'SUMMER:0.23'","Subseasons override seasons", "Allowed seasons: " + Stream.concat(Arrays.stream(Season.values()), Arrays.stream(Season.SubSeason.values())).map(Enum::name).collect(Collectors.joining(","))).defineList("seasonFallRate", defaults(), string -> string instanceof String && exists(((String) string)));
        builder.pop();
    }

    public void updateCache() {
        enabledSeasons = seasonFallRate.get().stream().map(this::create).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    public float getModifier(Level level) {
        ISeasonState state = SeasonHelper.getSeasonState(level);
        return enabledSeasons.containsKey(state.getSubSeason()) ? enabledSeasons.get(state.getSubSeason()) : enabledSeasons.getOrDefault(state.getSeason(), 1F);
    }

    private List<? extends String> defaults() {
        return Lists.newArrayList(Pair.of(Season.SPRING, 0), Pair.of(Season.AUTUMN, 1), Pair.of(Season.SUMMER, 0.2), Pair.of(Season.WINTER, 0.2)).stream().map(p -> p.getKey().name()+":"+p.getValue()).collect(Collectors.toList());
    }

    public boolean exists(String string) {
        try {
            create(string);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public Pair<Enum<?>, Float> create(String string){
        String[] split = string.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid format");
        }
        float modifier = Float.parseFloat(split[1]);
        Enum<?> season;
        try {
            season = Season.valueOf(split[0]);
        } catch (IllegalArgumentException e) {
            try {
                season = Season.SubSeason.valueOf(split[0]);
            } catch (IllegalArgumentException e2) {
                throw new IllegalArgumentException("Season could not be found");
            }
        }
        return Pair.of(season, modifier);
    }
}
