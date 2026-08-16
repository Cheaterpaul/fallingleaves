package de.cheaterpaul.fallingleaves.data.provider;

import com.mojang.logging.LogUtils;
import de.cheaterpaul.fallingleaves.particle.ColoredSpriteProvider;
import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafSetting;
import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafType;
import de.cheaterpaul.fallingleaves.leaves.mod.util.RenderSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class LeafProvider implements PreparableReloadListener {

    public static final Identifier LEAF_LISTENER = Identifier.fromNamespaceAndPath(FallingLeavesMod.MODID, "leaves");
    private static final Logger LOGGER = LogUtils.getLogger();


    private final TextureAtlas textureAtlas;
    private final LeafSettingProvider leafSettingProvider;
    private final LeafTypeProvider leafTypeProvider;

    private Map<Identifier, LeafType.LoadedLeafType> loadedLeaves = Map.of();
    private Map<Identifier, LeafSetting.LoadedLeafSetting> loadedSettings = Map.of();

    public LeafProvider() {
        this.textureAtlas = new TextureAtlas(RenderSettings.LEAVES_ATLAS);
        Minecraft.getInstance().getTextureManager().register(this.textureAtlas.location(), this.textureAtlas);
        this.leafSettingProvider = new LeafSettingProvider();
        this.leafTypeProvider = new LeafTypeProvider();
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(SharedState sharedState, Executor executor, PreparationBarrier preparationBarrier, Executor executor1) {
        CompletableFuture<Void> settingsReload = this.leafSettingProvider.reload(sharedState, executor, preparationBarrier, executor1);
        CompletableFuture<Void> typesReload = this.leafTypeProvider.reload(sharedState, executor, preparationBarrier, executor1);


        CompletableFuture<SpriteLoader.Preparations> preparations = SpriteLoader.create(this.textureAtlas).loadAndStitch(sharedState.resourceManager(), RenderSettings.LEAVES_ATLAS, 0, executor, Set.of());
        return CompletableFuture.allOf(settingsReload, typesReload, preparations).thenCompose(preparationBarrier::wait).thenAcceptAsync(param -> {
            SpriteLoader.Preparations spriteloader$preparations = preparations.join();
            this.textureAtlas.upload(spriteloader$preparations);
            var spriteSets = new HashMap<Identifier, LeafType.LoadedLeafType>();
            var leafSprites = new HashMap<Identifier, LeafSetting.LoadedLeafSetting>();
            ColoredSpriteProvider.TextureSprite notFound = new ColoredSpriteProvider.TextureSprite(spriteloader$preparations.missing(), true, 1);

            this.leafTypeProvider.getLeafTypes().forEach((key, leafType) -> {
                List<ColoredSpriteProvider.TextureSprite> list = new ArrayList<>();
                for (LeafType.Texture texture : leafType.textures()) {
                    TextureAtlasSprite sprite = spriteloader$preparations.regions().get(texture.texture());
                    if (sprite != null) {
                        list.add(new ColoredSpriteProvider.TextureSprite(sprite, texture.isTinted(), texture.sizeModifier()));
                    } else {
                        list.add(notFound);
                        LOGGER.warn("Missing leaves particle texture: {}", texture);
                    }
                }

                var spriteSet = new ColoredSpriteProvider(list);

                var settings = new LeafType.LoadedLeafType(leafType, spriteSet);
                spriteSets.put(key, settings);
            });

            this.leafSettingProvider.getLeafSettings().forEach((key, leafSetting) -> {
                LeafType.LoadedLeafType loadedLeafType = spriteSets.get(leafSetting.leafType());
                if (loadedLeafType == null) {
                    LOGGER.warn("Missing leaf type {} for leaf setting {}", leafSetting.leafType(), key);
                } else {
                    leafSprites.put(key, new LeafSetting.LoadedLeafSetting(leafSetting, loadedLeafType));
                }
            });


            this.loadedLeaves = Collections.unmodifiableMap(spriteSets);
            this.loadedSettings = Collections.unmodifiableMap(leafSprites);
        }, executor1);
    }

    public Map<Identifier, LeafType.LoadedLeafType> getLoadedLeaves() {
        return loadedLeaves;
    }

    public Map<Identifier, LeafSetting.LoadedLeafSetting> getLoadedSettings() {
        return loadedSettings;
    }
}
