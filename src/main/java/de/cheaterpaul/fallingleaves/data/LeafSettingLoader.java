package de.cheaterpaul.fallingleaves.data;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LeafSettingLoader extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private Map<ResourceLocation, LeafSettingsEntry> treeLeaveSizeValues = new HashMap<>();

    public LeafSettingLoader() {
        super(GSON, "fallingleaves/settings");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> values, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        this.treeLeaveSizeValues = values.entrySet().stream().<Pair<ResourceLocation,LeafSettingsEntry>>mapMulti((entry, consumer) -> {
            DataResult<Pair<LeafSettingsEntry, JsonElement>> decode = LeafSettingsEntry.CODEC.decode(JsonOps.INSTANCE, entry.getValue());
            decode.result().ifPresent(res -> consumer.accept(Pair.of(entry.getKey(), res.getFirst())));
            decode.error().ifPresent(error -> {
                LOGGER.error(error.message());
            });
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
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
