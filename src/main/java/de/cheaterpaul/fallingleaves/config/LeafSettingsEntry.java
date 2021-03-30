package de.cheaterpaul.fallingleaves.config;

import net.minecraft.util.ResourceLocation;

public class LeafSettingsEntry {

    public final ResourceLocation id;
    public final double spawnRateFactor;
    public final boolean isConiferBlock;

    public LeafSettingsEntry(ResourceLocation identifier, double spawnRateFactor, boolean isConiferBlock) {
        this.id = identifier;
        this.spawnRateFactor = spawnRateFactor;
        this.isConiferBlock = isConiferBlock;
    }

    public double getSpawnChance() {
        return spawnRateFactor;
    }

    @Override
    public String toString() {
        return String.format("LeafSettingsEntry{spawnRateFactor=%s, isConiferBlock=%s}",
            spawnRateFactor,
            isConiferBlock);
    }

}
