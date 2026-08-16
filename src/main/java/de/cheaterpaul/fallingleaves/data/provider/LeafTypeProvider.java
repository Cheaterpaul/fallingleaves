package de.cheaterpaul.fallingleaves.data.provider;

import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafType;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public class LeafTypeProvider extends SimpleJsonResourceReloadListener<LeafType> {

    private Map<Identifier, LeafType> leafTypes = Map.of();

    public LeafTypeProvider() {
        super(LeafType.CODEC, FileToIdConverter.json("fallingleaves/leaftypes"));
    }

    @Override
    protected void apply(@NotNull Map<Identifier, LeafType> IdentifierLeafSettingMap, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        this.leafTypes = Collections.unmodifiableMap(IdentifierLeafSettingMap);
    }

    public Map<Identifier, LeafType> getLeafTypes() {
        return this.leafTypes;
    }
}
