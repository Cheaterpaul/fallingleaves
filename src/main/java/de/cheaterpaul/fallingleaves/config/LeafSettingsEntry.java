package de.cheaterpaul.fallingleaves.config;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cheaterpaul.fallingleaves.init.ClientMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record LeafSettingsEntry(double spawnRateFactor, Optional<ResourceLocation> leafType, boolean considerAsConifer) {
    public static final Codec<LeafSettingsEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.strictOptionalField(Codec.DOUBLE, "spawnrate", 1.0).forGetter(LeafSettingsEntry::spawnRateFactor),
            ExtraCodecs.strictOptionalField(ResourceLocation.CODEC, "leaf_type").forGetter(s -> s.leafType),
            ExtraCodecs.strictOptionalField(Codec.BOOL, "consider_as_conifer", false).forGetter(s -> s.considerAsConifer)
    ).apply(inst, LeafSettingsEntry::new));

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public LeafSettingsEntry(double spawnRateFactor, Optional<ResourceLocation> leaf_type, Optional<Boolean> consider_as_conifer) {
        this(spawnRateFactor, leaf_type, consider_as_conifer.orElse(false));
    }

    public LeafSettingsEntry(double spawnRateFactor, boolean considerAsConifer) {
        this(spawnRateFactor, Optional.empty(), considerAsConifer);
    }

    public LeafSettingsEntry(double spawnRateFactor) {
        this(spawnRateFactor, Optional.empty(), false);
    }

    public LeafSettingsEntry(double spawnRateFactor, ResourceLocation leafType) {
        this(spawnRateFactor, Optional.of(leafType), ClientMod.CONIFER.equals(leafType));
    }

    public LeafSettingsEntry(double spawnRateFactor, boolean considerAsConifer, @Nullable ResourceLocation leafType) {
        this(spawnRateFactor, Optional.ofNullable(leafType), considerAsConifer);
    }
}
