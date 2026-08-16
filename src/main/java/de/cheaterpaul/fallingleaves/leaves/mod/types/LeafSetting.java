package de.cheaterpaul.fallingleaves.leaves.mod.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record LeafSetting(@NotNull Identifier leafType, double spawnRate) {

    public static final Codec<LeafSetting> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Identifier.CODEC.optionalFieldOf("leaf_type", LeafTypes.DEFAULT).forGetter(LeafSetting::leafType),
            Codec.DOUBLE.optionalFieldOf("spawnrate", 1d).forGetter(LeafSetting::spawnRate)
    ).apply(inst, LeafSetting::new));

    public LeafSetting(@NotNull Identifier leafType) {
        this(leafType, 1d);
    }

    public record LoadedLeafSetting(@NotNull LeafSetting setting, @NotNull LeafType.LoadedLeafType leafType) {}
}
