package de.cheaterpaul.fallingleaves.leaves.mod.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cheaterpaul.fallingleaves.particle.ColoredSpriteProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.Stream;

public record LeafType(List<Texture> textures, float spawnModifier, float sizeModifier, float lifeSpanModifier) {

    public static final Codec<LeafType> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Texture.CODEC.listOf().fieldOf("textures").forGetter(LeafType::textures),
            Codec.FLOAT.optionalFieldOf("spawnModifier", 1f).forGetter(LeafType::spawnModifier),
            Codec.FLOAT.optionalFieldOf("sizeModifier", 1f).forGetter(LeafType::sizeModifier),
            Codec.FLOAT.optionalFieldOf("lifeSpanModifier", 1f).forGetter(LeafType::lifeSpanModifier)
    ).apply(inst, LeafType::new));

    public LeafType(ResourceLocation... textures) {
        this(Stream.of(textures).map(Texture::new).toList(), 1f, 1f, 1f);
    }

    public LeafType(float spawnModifier, float sizeModifier, float lifeSpanModifier, ResourceLocation... textures) {
        this(Stream.of(textures).map(Texture::new).toList(), spawnModifier, sizeModifier, lifeSpanModifier);
    }

    public record LoadedLeafType(LeafType type, ColoredSpriteProvider spriteSet) {

    }

    public record Texture(ResourceLocation texture, boolean isTinted, float sizeModifier) {

        public static final Codec<Texture> CODEC = Codec.withAlternative(RecordCodecBuilder.create(inst -> inst.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Texture::texture),
                Codec.BOOL.optionalFieldOf("isTinted", false).forGetter(Texture::isTinted),
                Codec.FLOAT.optionalFieldOf("size", 1f).forGetter(Texture::sizeModifier)
        ).apply(inst, Texture::new)), ResourceLocation.CODEC.xmap(x -> new Texture(x, false, 1), Texture::texture));

        public Texture(ResourceLocation texture) {
            this(texture, false, 1);
        }

        public Texture(ResourceLocation texture, boolean isTinted) {
            this(texture, isTinted, 1);
        }
    }
}
