package de.cheaterpaul.fallingleaves.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
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
    private final Map<ResourceLocation, ParticleEngine.MutableSpriteSet> spriteSets = Maps.newHashMap();

    @Nullable
    public SpriteSet getSpriteSet(ResourceLocation leafType) {
        return spriteSets.get(leafType);
    }
    public LeafTypeLoader(TextureManager manager) {
        this.textureAtlas = new TextureAtlas(LEAVES_ATLAS);
        manager.register(this.textureAtlas.location(), this.textureAtlas);
    }
    @Override
    public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier stage, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller p_10640_, @NotNull ProfilerFiller p_10641_, @NotNull Executor pBackgroundExecutor, @NotNull Executor p_10643_) {
        record LeafType(ResourceLocation type, Collection<ResourceLocation> textures) {
            public LeafType(ResourceLocation type) {
                this(type, Collections.emptyList());
            }
        }
        CompletableFuture<Collection<LeafType>> textures = CompletableFuture.supplyAsync(() -> PARTICLE_LISTER.listMatchingResources(resourceManager).entrySet(), pBackgroundExecutor).thenApplyAsync(list -> {
            return list.stream().map(entry -> {
                var key = PARTICLE_LISTER.fileToId(entry.getKey());
                try (Reader reader = entry.getValue().openAsReader()) {
                    JsonObject object = GsonHelper.parse(reader);
                    return new LeafType(key, StreamSupport.stream(object.get("textures").getAsJsonArray().spliterator(), false).map(JsonElement::getAsString).map(ResourceLocation::parse).toList());
                } catch (IOException e) {
                    return new LeafType(key);
                }
            }).collect(Collectors.toList());
        });
        CompletableFuture<SpriteLoader.Preparations> preparations = SpriteLoader.create(this.textureAtlas).loadAndStitch(resourceManager, LEAVES_ATLAS, 0, pBackgroundExecutor).thenCompose(SpriteLoader.Preparations::waitForUpload);
        return CompletableFuture.allOf(textures, preparations).thenCompose(stage::wait).thenAcceptAsync(param -> {
            SpriteLoader.Preparations spriteloader$preparations = preparations.join();
            this.textureAtlas.upload(spriteloader$preparations);
            Set<ResourceLocation> set = new HashSet<>();
            Set<ResourceLocation> existingSprites = new HashSet<>(this.spriteSets.keySet());
            TextureAtlasSprite textureatlassprite = spriteloader$preparations.missing();
            textures.join().forEach(leafType -> {
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

                    ParticleEngine.MutableSpriteSet mutableSpriteSet = this.spriteSets.get(leafType.type);
                    existingSprites.remove(leafType.type);
                    if (mutableSpriteSet == null) {
                        mutableSpriteSet = new ParticleEngine.MutableSpriteSet();
                        this.spriteSets.put(leafType.type, mutableSpriteSet);
                    }
                    mutableSpriteSet.rebind(list);
                }

            });
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
