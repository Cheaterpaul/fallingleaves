package de.cheaterpaul.fallingleaves.data;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LeafSettingLoader extends SimpleJsonResourceReloadListener<LeafSettingsEntry> {

    private static final Logger LOGGER = LogManager.getLogger();
    private Map<ResourceLocation, LeafSettingsEntry> treeLeaveSizeValues = new HashMap<>();

    public LeafSettingLoader() {
        super(LeafSettingsEntry.CODEC, "fallingleaves/settings");
    }

    @Override
    protected void apply(Map<ResourceLocation, LeafSettingsEntry> values, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        this.treeLeaveSizeValues = values;
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
