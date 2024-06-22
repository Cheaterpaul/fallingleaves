package de.cheaterpaul.fallingleaves.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LeafTypeLoader implements PreparableReloadListener {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ResourceLocation LEAVES_ATLAS = ResourceLocation.fromNamespaceAndPath("fallingleaves", "leaves");
    private static final FileToIdConverter PARTICLE_LISTER = FileToIdConverter.json("fallingleaves/leaftypes");

    private final TextureAtlas textureAtlas;
    private final Map<ResourceLocation, LeafTypeSettings> spriteSets = Maps.newHashMap();
    private Map<ResourceLocation, LeafType> leafTypes = Maps.newHashMap();

    @Nullable
    public LeafTypeSettings getSpriteSet(ResourceLocation leafType) {
        return spriteSets.get(leafType);
    }
    public LeafTypeLoader(TextureManager manager) {
        this.textureAtlas = new TextureAtlas(LEAVES_ATLAS);
        manager.register(this.textureAtlas.location(), this.textureAtlas);
    }

    public record LeafType(Collection<ResourceLocation> textures, float sizeModifier, float lifeSpanModifier) {
        public static final Codec<LeafType> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ResourceLocation.CODEC.listOf().fieldOf("textures").forGetter(l -> new ArrayList<>(l.textures)),
                Codec.FLOAT.optionalFieldOf("sizeModifier").forGetter(l -> Optional.of(l.sizeModifier)),
                Codec.FLOAT.optionalFieldOf( "lifeSpanModifier").forGetter(l -> Optional.of(l.lifeSpanModifier))
        ).apply(inst, LeafType::new));

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public LeafType(Collection<ResourceLocation> textures, Optional<Float> sizeModifier, Optional<Float> lifeSpanModifier) {
            this(textures, sizeModifier.orElse(1.0f), lifeSpanModifier.orElse(1.0f));
        }

        public LeafType() {
            this(Collections.emptyList(), 1.0f, 1.0f);
        }
    }

    public static class LeafTypeSettings {
        private final ParticleEngine.MutableSpriteSet spriteSet;
        private LeafType leafType;

        public LeafTypeSettings(ParticleEngine.MutableSpriteSet spriteSet, LeafType leafType) {
            this.spriteSet = spriteSet;
            this.leafType = leafType;
        }

        public LeafType getLeafType() {
            return leafType;
        }

        public ParticleEngine.MutableSpriteSet getSpriteSet() {
            return spriteSet;
        }
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier stage, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller p_10640_, @NotNull ProfilerFiller p_10641_, @NotNull Executor pBackgroundExecutor, @NotNull Executor p_10643_) {

        CompletableFuture<Map<ResourceLocation, LeafType>> textures = CompletableFuture.supplyAsync(() -> PARTICLE_LISTER.listMatchingResources(resourceManager).entrySet(), pBackgroundExecutor).thenApplyAsync(list -> {
            return list.stream().collect(Collectors.toMap(entry -> PARTICLE_LISTER.fileToId(entry.getKey()), entry -> {
                try (Reader reader = entry.getValue().openAsReader()) {
                    JsonObject object = GsonHelper.parse(reader);
                    return LeafType.CODEC.decode(JsonOps.INSTANCE, object).result().map(Pair::getFirst).orElseGet(LeafType::new);
                } catch (IOException e) {
                    return new LeafType();
                }
            }));
        });
        CompletableFuture<SpriteLoader.Preparations> preparations = SpriteLoader.create(this.textureAtlas).loadAndStitch(resourceManager, LEAVES_ATLAS, 0, pBackgroundExecutor).thenCompose(SpriteLoader.Preparations::waitForUpload);
        return CompletableFuture.allOf(textures, preparations).thenCompose(stage::wait).thenAcceptAsync(param -> {
            SpriteLoader.Preparations spriteloader$preparations = preparations.join();
            this.textureAtlas.upload(spriteloader$preparations);
            Set<ResourceLocation> set = new HashSet<>();
            Set<ResourceLocation> existingSprites = new HashSet<>(this.spriteSets.keySet());
            TextureAtlasSprite textureatlassprite = spriteloader$preparations.missing();
            textures.join().forEach((key, leafType) -> {
                if (!leafType.textures.isEmpty()) {
                    List<TextureAtlasSprite> list = new ArrayList<>();

                    for (ResourceLocation resourceLocation : leafType.textures) {
                        TextureAtlasSprite sprite = spriteloader$preparations.regions().get(resourceLocation);
                        if (sprite == null) {
                            set.add(resourceLocation);
                            list.add(textureatlassprite);
                        } else {
                            list.add(sprite);
                        }
                    }

                    LeafTypeSettings settings = this.spriteSets.get(key);
                    existingSprites.remove(key);
                    if (settings == null) {
                        settings = new LeafTypeSettings(new ParticleEngine.MutableSpriteSet(), leafType);
                        this.spriteSets.put(key, settings);
                    }
                    settings.spriteSet.rebind(list);
                    settings.leafType = leafType;
                }

            });
            leafTypes = textures.join();
            existingSprites.forEach(this.spriteSets::remove);
            if (!set.isEmpty()) {
                LOGGER.warn("Missing particle sprites: {}", set.stream().sorted().map(ResourceLocation::toString).collect(Collectors.joining(",")));
            }
        }, p_10643_);
    }

    public void close() {
        this.textureAtlas.clearTextureData();
    }

    @Override
    public @NotNull String getName() {
        return "Leaf Type";
    }
}
