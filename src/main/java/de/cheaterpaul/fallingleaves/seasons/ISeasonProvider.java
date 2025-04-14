package de.cheaterpaul.fallingleaves.seasons;

import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafType;
import org.jetbrains.annotations.Nullable;

public interface ISeasonProvider {

    ISeasonProvider DEFAULT = new ISeasonProvider() {
        @Override
        public @Nullable Season.SubSeason getCurrentSeason() {
            return null;
        }

        @Override
        public float getSeasonModifier(LeafType type) {
            return 1f;
        }
    };

    @Nullable
    Season.SubSeason getCurrentSeason();

    float getSeasonModifier(LeafType type);
}
