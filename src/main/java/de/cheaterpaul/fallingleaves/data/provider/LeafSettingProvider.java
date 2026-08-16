package de.cheaterpaul.fallingleaves.data.provider;

import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafSetting;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class LeafSettingProvider extends SimpleJsonResourceReloadListener<LeafSetting> {

    private Map<Identifier, LeafSetting> leafSettings = Map.of();

    public LeafSettingProvider() {
        super(LeafSetting.CODEC, FileToIdConverter.json("fallingleaves/settings"));
    }


    @Override
    protected void apply(@NotNull Map<Identifier, LeafSetting> IdentifierLeafSettingMap, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        DefaultedRegistry<Block> registry = BuiltInRegistries.BLOCK;
        this.leafSettings = IdentifierLeafSettingMap.entrySet().stream().filter(x -> registry.containsKey(x.getKey())).collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<Identifier, LeafSetting> getLeafSettings() {
        return this.leafSettings;
    }
}
