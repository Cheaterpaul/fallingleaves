package de.cheaterpaul.fallingleaves.leaves.mod.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record LeafType(List<ResourceLocation> textures, float spawnModifier, float sizeModifier, float lifeSpanModifier) {

    public static final Codec<LeafType> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.listOf().fieldOf("textures").forGetter(LeafType::textures),
            Codec.FLOAT.optionalFieldOf("spawnModifier", 1f).forGetter(LeafType::spawnModifier),
            Codec.FLOAT.optionalFieldOf("sizeModifier", 1f).forGetter(LeafType::sizeModifier),
            Codec.FLOAT.optionalFieldOf("lifeSpanModifier", 1f).forGetter(LeafType::lifeSpanModifier)
    ).apply(inst, LeafType::new));

    public LeafType(ResourceLocation... textures) {
        this(List.of(textures), 1f, 1f, 1f);
    }

    public record LoadedLeafType(LeafType type, ParticleEngine.MutableSpriteSet spriteSet) {

    }
}
