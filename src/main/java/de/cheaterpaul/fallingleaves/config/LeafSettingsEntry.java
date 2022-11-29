package de.cheaterpaul.fallingleaves.config;

import net.minecraft.resources.ResourceLocation;

public record LeafSettingsEntry(ResourceLocation id, double spawnRateFactor, ResourceLocation leafType, boolean considerAsConifer) {
    public LeafSettingsEntry(ResourceLocation id, double spawnRateFactor, boolean isConiferBlock) {
        this(id, spawnRateFactor, id, isConiferBlock);
    }

    public LeafSettingsEntry(ResourceLocation id, double spawnRateFactor) {
        this(id, spawnRateFactor, id, false);
    }
}
