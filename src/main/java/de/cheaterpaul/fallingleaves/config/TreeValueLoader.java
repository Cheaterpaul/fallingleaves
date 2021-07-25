package de.cheaterpaul.fallingleaves.config;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        super(GSON, "fallingleaves");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> values, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, LeafSettingsEntry> map = new HashMap<>();
        values.forEach((id, json) -> {
            try {
                JsonObject object = GsonHelper.convertToJsonObject(json, "leafsettings");
                double spawnrate = 1;
                if (object.has("spawnrate")) {
                    spawnrate = object.get("spawnrate").getAsDouble();
                }
                boolean isConifer = false;
                if (object.has("isConifer")) {
                    isConifer = object.get("isConifer").getAsBoolean();
                }
                map.put(id, new LeafSettingsEntry(id, spawnrate, isConifer));
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
