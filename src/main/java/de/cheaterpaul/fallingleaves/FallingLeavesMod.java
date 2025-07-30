package de.cheaterpaul.fallingleaves;

import com.mojang.logging.LogUtils;
import de.cheaterpaul.fallingleaves.config.Config;
import de.cheaterpaul.fallingleaves.data.provider.LeafProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

@Mod(value = FallingLeavesMod.MODID, dist = Dist.CLIENT)
public class FallingLeavesMod {
    public static final String MODID = "fallingleaves";

    public FallingLeavesMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CONFIG_SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public static ResourceLocation modId(String string) {
        return ResourceLocation.fromNamespaceAndPath(MODID, string);
    }

}
