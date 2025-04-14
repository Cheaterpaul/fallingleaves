package de.cheaterpaul.fallingleaves.leaves.mod.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cheaterpaul.fallingleaves.particle.ColoredSpriteProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.util.List;
import java.util.stream.Stream;

public record LeafType(List<Texture> textures, float spawnModifier, float sizeModifier, float lifeSpanModifier, SeasonModifier seasonModifier) {

    public static final Codec<LeafType> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Texture.CODEC.listOf().fieldOf("textures").forGetter(LeafType::textures),
            Codec.FLOAT.optionalFieldOf("spawnModifier", 1f).forGetter(LeafType::spawnModifier),
            Codec.FLOAT.optionalFieldOf("sizeModifier", 1f).forGetter(LeafType::sizeModifier),
            Codec.FLOAT.optionalFieldOf("lifeSpanModifier", 1f).forGetter(LeafType::lifeSpanModifier),
            SeasonModifier.CODEC.optionalFieldOf("season", SeasonModifier.DEFAULT).forGetter(LeafType::seasonModifier)
    ).apply(inst, LeafType::new));

    public LeafType(float spawnModifier, float sizeModifier, float lifeSpanModifier, ResourceLocation... textures) {
        this(spawnModifier, sizeModifier, lifeSpanModifier, SeasonModifier.DEFAULT, textures);
    }

    public LeafType(float spawnModifier, float sizeModifier, float lifeSpanModifier, SeasonModifier seasonModifier, ResourceLocation... textures) {
        this(Stream.of(textures).map(Texture::new).toList(), spawnModifier, sizeModifier, lifeSpanModifier, seasonModifier);
    }

    public LeafType(float spawnModifier, float sizeModifier, float lifeSpanModifier, Texture... textures) {
        this(spawnModifier, sizeModifier, lifeSpanModifier, SeasonModifier.DEFAULT, textures);
    }

    public LeafType(float spawnModifier, float sizeModifier, float lifeSpanModifier, SeasonModifier seasonModifier, Texture... textures) {
        this(Stream.of(textures).toList(), spawnModifier, sizeModifier, lifeSpanModifier, seasonModifier);
    }

    public record LoadedLeafType(LeafType type, ColoredSpriteProvider spriteSet) {

    }

    public record Texture(ResourceLocation texture, boolean isTinted, float sizeModifier) {

        public static final Codec<Texture> CODEC = NeoForgeExtraCodecs.withAlternative(ResourceLocation.CODEC.flatXmap(x -> DataResult.success(new Texture(x, false, 1)), x -> {
                    if (!x.isTinted && x.sizeModifier == 1) {
                        return DataResult.success(x.texture);
                    }
                    return DataResult.error(() -> "can not serialize with different isTinted and sizeModifier values");
                }),
                RecordCodecBuilder.create(inst -> inst.group(
                        ResourceLocation.CODEC.fieldOf("texture").forGetter(Texture::texture),
                        Codec.BOOL.optionalFieldOf("isTinted", false).forGetter(Texture::isTinted),
                        Codec.FLOAT.optionalFieldOf("size", 1f).forGetter(Texture::sizeModifier)
                ).apply(inst, Texture::new)));

        public Texture(ResourceLocation texture) {
            this(texture, false, 1);
        }

        public Texture(ResourceLocation texture, boolean isTinted) {
            this(texture, isTinted, 1);
        }
    }

    public record SeasonModifier(Season spring, Season summer, Season autumn, Season winter) {

        public static final SeasonModifier DEFAULT = new SeasonModifier(new Season(0,0,0), new Season(0,0.4f,0.8f), new Season(1.0f,1.5f,1f), new Season(0,0,0));

        public static final Codec<SeasonModifier> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Season.CODEC.fieldOf("spring").forGetter(SeasonModifier::spring),
                Season.CODEC.fieldOf("summer").forGetter(SeasonModifier::summer),
                Season.CODEC.fieldOf("autumn").forGetter(SeasonModifier::autumn),
                Season.CODEC.fieldOf("winter").forGetter(SeasonModifier::winter)
        ).apply(inst, SeasonModifier::new));
    }

    public record Season(float early, float mid, float late) {

        public static final Codec<Season> CODEC = NeoForgeExtraCodecs.withAlternative(Codec.FLOAT.flatXmap(x -> DataResult.success(new Season(x, x, x)), season -> {
            if (season.early == season.mid && season.mid == season.late) {
                return DataResult.success(season.mid);
            }
            return DataResult.error(() -> "can not serialize with different early, mid and late values");
        }), RecordCodecBuilder.create(inst -> inst.group(
                Codec.FLOAT.fieldOf("early").forGetter(Season::early),
                Codec.FLOAT.fieldOf("mid").forGetter(Season::mid),
                Codec.FLOAT.fieldOf("late").forGetter(Season::late)
        ).apply(inst, Season::new)));

        public Season(float mid) {
            this(mid, mid, mid);
        }
    }
}
