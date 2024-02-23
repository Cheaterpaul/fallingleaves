package de.cheaterpaul.fallingleaves.init;

import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import de.cheaterpaul.fallingleaves.data.LeafSettingGenerator;
import de.cheaterpaul.fallingleaves.data.LeafSettingLoader;
import de.cheaterpaul.fallingleaves.data.LeafTypeLoader;
//import de.cheaterpaul.fallingleaves.modcompat.SereneSeasons;
import de.cheaterpaul.fallingleaves.util.TextureCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class ClientMod {

    public static final ResourceLocation DEFAULT = new ResourceLocation("fallingleaves", "default");
    public static final ResourceLocation CONIFER = new ResourceLocation("fallingleaves", "conifer");
    private static LeafTypeLoader leafTypeLoader;
    private static LeafSettingLoader treeValueLoader;

    public static SpriteSet getSpriteForLeafType(ResourceLocation leafType) {
        return leafTypeLoader.getSpriteSet(leafType);
    }

    public static LeafSettingsEntry getLeafSetting(ResourceLocation location) {
        return treeValueLoader.getLeafSetting(location);
    }

    private static void gatherData(final GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeClient(), new LeafSettingGenerator(event.getGenerator().getPackOutput()));
    }

    public static void setupClient(IEventBus modBus){
        modBus.addListener(ClientMod::gatherData);
        modBus.addListener(ClientMod::registerReloadListeners);
        modBus.addListener(ClientMod::registerReloadListeners);
        modBus.addListener(ClientMod::onReload);
//        if (SereneSeasons.setup()) {
//            bus.register(SereneSeasons.class);
//        }
        FallingLeavesConfig.registerConfigs();
        NeoForge.EVENT_BUS.register(new EventHandler());
    }

    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(leafTypeLoader = new LeafTypeLoader(Minecraft.getInstance().getTextureManager()));
        event.registerReloadListener(treeValueLoader = new LeafSettingLoader());
    }

    public static void onReload(TextureAtlasStitchedEvent event) {
        TextureCache.INST.clear();
    }
}
