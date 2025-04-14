package de.cheaterpaul.fallingleaves.seasons.serene;

import com.google.common.collect.EnumBiMap;
import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafType;
import de.cheaterpaul.fallingleaves.seasons.ISeasonProvider;
import de.cheaterpaul.fallingleaves.seasons.Season;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import sereneseasons.api.season.SeasonHelper;

import java.util.HashMap;

public class SereneSeasonProvider implements ISeasonProvider {

    private final ClientLevel level;

    private final EnumBiMap<sereneseasons.api.season.Season.SubSeason, Season.SubSeason> seasonMap = EnumBiMap.create(new HashMap<>() {{
        put(sereneseasons.api.season.Season.SubSeason.EARLY_SPRING, Season.SubSeason.EARLY_SPRING);
        put(sereneseasons.api.season.Season.SubSeason.MID_SPRING, Season.SubSeason.MID_SPRING);
        put(sereneseasons.api.season.Season.SubSeason.LATE_SPRING, Season.SubSeason.LATE_SPRING);
        put(sereneseasons.api.season.Season.SubSeason.EARLY_SUMMER, Season.SubSeason.EARLY_SUMMER);
        put(sereneseasons.api.season.Season.SubSeason.MID_SUMMER, Season.SubSeason.MID_SUMMER);
        put(sereneseasons.api.season.Season.SubSeason.LATE_SUMMER, Season.SubSeason.LATE_SUMMER);
        put(sereneseasons.api.season.Season.SubSeason.EARLY_AUTUMN, Season.SubSeason.EARLY_AUTUMN);
        put(sereneseasons.api.season.Season.SubSeason.MID_AUTUMN, Season.SubSeason.MID_AUTUMN);
        put(sereneseasons.api.season.Season.SubSeason.LATE_AUTUMN, Season.SubSeason.LATE_AUTUMN);
        put(sereneseasons.api.season.Season.SubSeason.EARLY_WINTER, Season.SubSeason.EARLY_WINTER);
        put(sereneseasons.api.season.Season.SubSeason.MID_WINTER, Season.SubSeason.MID_WINTER);
        put(sereneseasons.api.season.Season.SubSeason.LATE_WINTER, Season.SubSeason.LATE_WINTER);
    }
    });

    public SereneSeasonProvider(ClientLevel level) {
        this.level = level;
    }

    @Override
    public @Nullable Season.SubSeason getCurrentSeason() {
        return seasonMap.getOrDefault(SeasonHelper.getSeasonState(this.level).getSubSeason(), null);
    }

    @Override
    public float getSeasonModifier(LeafType type) {
        Season.SubSeason currentSeason = getCurrentSeason();
        if (currentSeason != null) {
            var modifier = switch (currentSeason.season) {
                case SPRING -> type.seasonModifier().spring();
                case SUMMER -> type.seasonModifier().summer();
                case AUTUMN -> type.seasonModifier().autumn();
                case WINTER -> type.seasonModifier().winter();
            };
            return switch (currentSeason.age) {
                case EARLY -> modifier.early();
                case MID -> modifier.mid();
                case LATE -> modifier.late();
            };
        }
        return 1f;
    }
}
