package de.cheaterpaul.fallingleaves.data;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = FallingLeavesMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LeafLoader {

    private static LeafTypeLoader leafTypeLoader;
    private static LeafSettingLoader treeValueLoader;

    private static final Map<LeafTypeLoader.LeafType, LeafTypeLoader.ParticleProvider> SNOW_CACHE = new HashMap<>();

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(leafTypeLoader = new LeafTypeLoader(Minecraft.getInstance().getTextureManager()));
        event.registerReloadListener(treeValueLoader = new LeafSettingLoader());
    }

    @SubscribeEvent
    public static void onReload(TextureAtlasStitchedEvent event) {
        SNOW_CACHE.clear();
    }

    public static LeafTypeLoader.LeafTypeSettings getSpriteForLeafType(ResourceLocation leafType) {
        return leafTypeLoader.getSpriteSet(leafType);
    }

    public static LeafTypeLoader.ParticleProvider getSnowParticleProvider(LeafTypeLoader.LeafTypeSettings settings) {
        return SNOW_CACHE.computeIfAbsent(settings.getLeafType(), (type) -> {
            var snowSetting = getSpriteForLeafType(FallingLeavesMod.SNOW);
            return new LeafTypeLoader.ParticleProvider() {
                @Override
                public ParticleEngine.MutableSpriteSet getSpriteSet() {
                    return snowSetting.getSpriteSet();
                }

                @Override
                public float sizeModifier() {
                    return snowSetting.sizeModifier() * settings.snowSizeModifier();
                }

                @Override
                public float lifeSpawnModifier() {
                    return snowSetting.lifeSpawnModifier();
                }
            };
        });
    }

    public static LeafSettingsEntry getLeafSetting(ResourceLocation location) {
        return treeValueLoader.getLeafSetting(location);
    }
}
