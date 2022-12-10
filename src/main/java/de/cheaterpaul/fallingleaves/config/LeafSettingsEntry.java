package de.cheaterpaul.fallingleaves.config;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public record LeafSettingsEntry(ResourceLocation id, double spawnRateFactor, ResourceLocation leafType, boolean considerAsConifer) {
    public LeafSettingsEntry(ResourceLocation id, double spawnRateFactor, boolean isConiferBlock) {
        this(id, spawnRateFactor, id, isConiferBlock);
    }

    public LeafSettingsEntry(ResourceLocation id, double spawnRateFactor) {
        this(id, spawnRateFactor, id, false);
    }

    public JsonObject serializeToJson() {
        JsonObject object = new JsonObject();
        object.addProperty("spawnrate", spawnRateFactor);
        object.addProperty("leaf_type", leafType.toString());
        object.addProperty("consider_as_conifer", considerAsConifer);
        return object;
    }
}
