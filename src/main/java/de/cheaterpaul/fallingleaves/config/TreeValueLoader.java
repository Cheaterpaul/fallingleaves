package de.cheaterpaul.fallingleaves.config;

import com.google.gson.*;
import de.cheaterpaul.fallingleaves.init.ClientMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TreeValueLoader extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private Map<ResourceLocation, LeafSettingsEntry> treeLeaveSizeValues = new HashMap<>();

    public TreeValueLoader() {
        super(GSON, "fallingleaves/settings");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> values, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<ResourceLocation, LeafSettingsEntry> map = new HashMap<>();
        values.forEach((id, json) -> {
            try {
                JsonObject object = GsonHelper.convertToJsonObject(json, "leafsettings");
                double spawn_rate = object.has("spawnrate") ? object.get("spawnrate").getAsDouble() : 1.0;
                ResourceLocation leafType = object.has("leaf_type") ? new ResourceLocation(object.get("leaf_type").getAsString()) : id;
                boolean considerAsConifer = false;
                if (object.has("isConifer") && object.get("isConifer").getAsBoolean()) {
                    leafType = ClientMod.CONIFER;
                    considerAsConifer = true;
                }
                considerAsConifer = object.has("consider_as_conifer") ? object.get("consider_as_conifer").getAsBoolean() : considerAsConifer;
                map.put(id, new LeafSettingsEntry(id, spawn_rate, leafType, considerAsConifer));
            } catch (IllegalArgumentException | JsonParseException e) {
                LOGGER.error("Parsing error loading leaf settings {}: {}", json, e.getMessage());
            }
        });
        this.treeLeaveSizeValues = map;
    }

    @Nullable
    public LeafSettingsEntry getLeafSetting(ResourceLocation loc) {
        return treeLeaveSizeValues.get(loc);
    }

    @Nonnull
    public Collection<LeafSettingsEntry> getALlSettings() {
        return this.treeLeaveSizeValues.values();
    }

}
