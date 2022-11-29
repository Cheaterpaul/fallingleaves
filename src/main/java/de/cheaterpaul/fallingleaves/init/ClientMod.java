package de.cheaterpaul.fallingleaves.init;

import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import de.cheaterpaul.fallingleaves.config.TreeValueLoader;
import de.cheaterpaul.fallingleaves.data.LeafSettingGenerator;
import de.cheaterpaul.fallingleaves.data.LeafTypeLoader;
import de.cheaterpaul.fallingleaves.modcompat.SereneSeasons;
import de.cheaterpaul.fallingleaves.util.TextureCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientMod {

    public static final ResourceLocation DEFAULT = new ResourceLocation("fallingleaves", "default");
    public static final ResourceLocation CONIFER = new ResourceLocation("fallingleaves", "conifer");
    private static LeafTypeLoader leafTypeLoader;
    private static TreeValueLoader treeValueLoader;

    public static SpriteSet getSpriteForLeafType(ResourceLocation leafType) {
        return leafTypeLoader.getSpriteSet(leafType);
    }

    public static LeafSettingsEntry getLeafSetting(ResourceLocation location) {
        return treeValueLoader.getLeafSetting(location);
    }

    private static void gatherData(final GatherDataEvent event) {
        if (event.includeClient()) {
            event.getGenerator().addProvider(event.includeServer(), new LeafSettingGenerator(event.getGenerator()));
        }
    }

    public static void setupClient(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ClientMod::gatherData);
        bus.addListener(ClientMod::registerReloadListeners);
        bus.addListener(ClientMod::registerReloadListeners);
        bus.addListener(ClientMod::onReload);
        if (SereneSeasons.setup()) {
            bus.register(SereneSeasons.class);
        }
        FallingLeavesConfig.registerConfigs();
    }

    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(leafTypeLoader = new LeafTypeLoader(Minecraft.getInstance().getTextureManager()));
        event.registerReloadListener(treeValueLoader = new TreeValueLoader());
    }

    public static void onReload(TextureStitchEvent.Post event) {
        TextureCache.INST.clear();
    }
}
