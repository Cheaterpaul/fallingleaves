package de.cheaterpaul.fallingleaves.data.provider;

import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafType;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public class LeafTypeProvider extends SimpleJsonResourceReloadListener<LeafType> {

    private Map<ResourceLocation, LeafType> leafTypes = Map.of();

    public LeafTypeProvider() {
        super(LeafType.CODEC, FileToIdConverter.json("fallingleaves/leaftypes"));
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, LeafType> resourceLocationLeafSettingMap, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        this.leafTypes = Collections.unmodifiableMap(resourceLocationLeafSettingMap);
    }

    public Map<ResourceLocation, LeafType> getLeafTypes() {
        return this.leafTypes;
    }
}
