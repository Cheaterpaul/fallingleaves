package de.cheaterpaul.fallingleaves.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LeafTypeLoader implements PreparableReloadListener {

    public static final ResourceLocation LEAVES_ATLAS = new ResourceLocation("fallingleaves", "leaves");
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
    public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier p_10638_, @NotNull ResourceManager p_10639_, @NotNull ProfilerFiller p_10640_, @NotNull ProfilerFiller p_10641_, @NotNull Executor p_10642_, @NotNull Executor p_10643_) {
        return CompletableFuture.supplyAsync(() -> p_10639_.listResources("fallingleaves/leaftypes", name -> name.getPath().endsWith(".json")).entrySet()).thenApplyAsync(list -> {
            Map<ResourceLocation, List<ResourceLocation>> textures = list.stream().collect(Collectors.toMap(entry -> new ResourceLocation(entry.getKey().getNamespace(), entry.getKey().getPath().substring(24, entry.getKey().getPath().length() - 5)), entry -> {
                try (Reader reader = entry.getValue().openAsReader()) {
                    JsonObject object = GsonHelper.parse(reader);
                    return StreamSupport.stream(object.get("textures").getAsJsonArray().spliterator(), false).map(JsonElement::getAsString).map(ResourceLocation::new).map(loc -> new ResourceLocation(loc.getNamespace(), "particle/" + loc.getPath())).toList();
                } catch (IOException e) {
                    return Collections.emptyList();
                }
            }));
            return Pair.of(textures, this.textureAtlas.prepareToStitch(p_10639_, textures.values().stream().flatMap(Collection::stream), p_10640_, 0));
        }, p_10642_).thenCompose(p_10638_::wait).thenAcceptAsync(preparations -> {
            this.textureAtlas.reload(preparations.getValue());
            TextureAtlasSprite missingSprite = this.textureAtlas.getSprite(MissingTextureAtlasSprite.getLocation());
            Set<ResourceLocation> existingSprites = new HashSet<>(this.spriteSets.keySet());
            preparations.getKey().forEach((key, value) -> {
                List<TextureAtlasSprite> sprites = value.isEmpty() ? List.of(missingSprite) : value.stream().map(this.textureAtlas::getSprite).toList();
                ParticleEngine.MutableSpriteSet mutableSpriteSet = this.spriteSets.get(key);
                existingSprites.remove(key);
                if (mutableSpriteSet == null) {
                    mutableSpriteSet = new ParticleEngine.MutableSpriteSet();
                    this.spriteSets.put(key, mutableSpriteSet);
                }
                mutableSpriteSet.rebind(sprites);
            });
            existingSprites.forEach(this.spriteSets::remove);
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
