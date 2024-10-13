package de.cheaterpaul.fallingleaves.data;

import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = "fallingleaves", bus = EventBusSubscriber.Bus.MOD)
public class LeafLoader {

    private static LeafTypeLoader leafTypeLoader;
    private static LeafSettingLoader treeValueLoader;

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(leafTypeLoader = new LeafTypeLoader(Minecraft.getInstance().getTextureManager()));
        event.registerReloadListener(treeValueLoader = new LeafSettingLoader());
    }

    public static LeafTypeLoader.LeafTypeSettings getSpriteForLeafType(ResourceLocation leafType) {
        return leafTypeLoader.getSpriteSet(leafType);
    }

    public static LeafSettingsEntry getLeafSetting(ResourceLocation location) {
        return treeValueLoader.getLeafSetting(location);
    }
}
