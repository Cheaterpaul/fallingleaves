package de.cheaterpaul.fallingleaves.modcompat;

import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("DeprecatedIsStillUsed")
public class SereneSeasonsConfig {

    private final EnumMap<Season, ModConfigSpec.DoubleValue> seasons = new EnumMap<>(Season.class);
    private final EnumMap<Season.SubSeason, ModConfigSpec.DoubleValue> subSeasons = new EnumMap<>(Season.SubSeason.class);

    @Deprecated(forRemoval = true)
    private final ModConfigSpec.ConfigValue<List<? extends String>> seasonFallRate;
    @Deprecated(forRemoval = true)
    private Map<Enum<?>, Float> enabledSeasons = new HashMap<>();

    public SereneSeasonsConfig(ModConfigSpec.Builder builder) {
        var spring = builder.comment("Spring spawn rate modifier.", "This modified is multiplied to the default spawn rate.").defineInRange("spring", 0, 0d, 5);
        var summer = builder.comment("Summer spawn rate modifier.", "This modifier is multiplied to the default spawn rate.").defineInRange("summer", 0.2, 0d, 5);
        var autumn = builder.comment("Autumn spawn rate modifier.", "This modifier is multiplied to the default spawn rate.").defineInRange("autumn", 1, 0d, 5);
        var winter = builder.comment("Winter spawn rate modifier.", "This modifier is multiplied to the default spawn rate.").defineInRange("winter", 0.3, 0d, 5);

        builder.comment("Specify sub season modifier.", "These Modifier are multiplied with the season modifier.").push("subseasons");

        var early_spring = builder.comment("Early Spring spawn rate modifier.", "This modified is multiplied to the season spawn rate.").defineInRange("early_spring", 1, 0d, 100);
        var mid_spring = builder.comment("Mid Spring spawn rate modifier.", "This modified is multiplied to the season spawn rate.").defineInRange("mid_spring", 1, 0d, 100);
        var late_spring = builder.comment("Late Spring spawn rate modifier.", "This modified is multiplied to the season spawn rate.").defineInRange("late_spring", 1, 0d, 100);
        var early_summer = builder.comment("Early Summer spawn rate modifier.", "This modified is multiplied to the season spawn rate.").defineInRange("early_summer", 1, 0d, 100);
        var mid_summer = builder.comment("Mid Summer spawn rate modifier.", "This modified is multiplied to the season spawn rate.").defineInRange("mid_summer", 1, 0d, 100);
        var late_summer = builder.comment("Late Summer spawn rate modifier.", "This modified is multiplied to the season spawn rate.").defineInRange("late_summer", 1, 0d, 100);
        var early_autumn = builder.comment("Early Autumn spawn rate modifier.", "This modified is multiplied to the season spawn rate.").defineInRange("early_autumn", 1, 0d, 100);
        var mid_autumn = builder.comment("Mid Autumn spawn rate modifier.", "This modified is multiplied to the season spawn rate.").defineInRange("mid_autumn", 1, 0d, 100);
        var late_autumn = builder.comment("Late Autumn spawn rate modifier.", "This modified is multiplied to the season spawn rate.").defineInRange("late_autumn", 1, 0d, 100);
        var early_winter = builder.comment("Early Winter spawn rate modifier.", "This modified is multiplied to the season spawn rate.").defineInRange("early_winter", 1, 0d, 100);
        var mid_winter = builder.comment("Mid Winter spawn rate modifier.", "This modified is multiplied to the season spawn rate.").defineInRange("mid_winter", 1, 0d, 100);
        var late_winter = builder.comment("Late Winter spawn rate modifier.", "This modified is multiplied to the season spawn rate.").defineInRange("late_winter", 1, 0d, 100);
        builder.pop();

        seasonFallRate = builder.comment("[Deprecated] spawnrate modifier per season/subseason", "Format: '<season>:<modifier>'; eg 'SUMMER:0.23'","Subseasons override seasons", "Allowed seasons: " + Stream.concat(Arrays.stream(Season.values()), Arrays.stream(Season.SubSeason.values())).map(Enum::name).collect(Collectors.joining(","))).defineList("seasonFallRate", new ArrayList<>(),() -> "", string -> string instanceof String && exists(((String) string)));

        seasons.put(Season.SPRING, spring);
        seasons.put(Season.SUMMER, summer);
        seasons.put(Season.AUTUMN, autumn);
        seasons.put(Season.WINTER, winter);

        subSeasons.put(Season.SubSeason.EARLY_SPRING, early_spring);
        subSeasons.put(Season.SubSeason.MID_SPRING, mid_spring);
        subSeasons.put(Season.SubSeason.LATE_SPRING, late_spring);
        subSeasons.put(Season.SubSeason.EARLY_SUMMER, early_summer);
        subSeasons.put(Season.SubSeason.MID_SUMMER, mid_summer);
        subSeasons.put(Season.SubSeason.LATE_SUMMER, late_summer);
        subSeasons.put(Season.SubSeason.EARLY_AUTUMN, early_autumn);
        subSeasons.put(Season.SubSeason.MID_AUTUMN, mid_autumn);
        subSeasons.put(Season.SubSeason.LATE_AUTUMN, late_autumn);
        subSeasons.put(Season.SubSeason.EARLY_WINTER, early_winter);
        subSeasons.put(Season.SubSeason.MID_WINTER, mid_winter);
        subSeasons.put(Season.SubSeason.LATE_WINTER, late_winter);
    }

    @Deprecated(forRemoval = true)
    public void updateCache() {
        enabledSeasons = seasonFallRate.get().stream().filter(x -> {
            try {
                create(x);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }).map(this::create).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    public float getModifier(Level level) {
        ISeasonState state = SeasonHelper.getSeasonState(level);
        if (enabledSeasons.containsKey(state.getSubSeason())) {
            return enabledSeasons.get(state.getSubSeason());
        }
        if (enabledSeasons.containsKey(state.getSeason())) {
            return enabledSeasons.get(state.getSeason());
        }
        return (float) (seasons.get(state.getSeason()).getAsDouble() * subSeasons.get(state.getSubSeason()).getAsDouble());
    }

    @Deprecated(forRemoval = true)
    public boolean exists(String string) {
        try {
            create(string);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Deprecated(forRemoval = true)
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
