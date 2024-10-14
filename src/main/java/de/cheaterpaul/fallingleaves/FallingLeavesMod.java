package de.cheaterpaul.fallingleaves;

import de.cheaterpaul.fallingleaves.config.ClientConfig;
import de.cheaterpaul.fallingleaves.data.LeafSettingGenerator;
import de.cheaterpaul.fallingleaves.data.LeafSettingLoader;
import de.cheaterpaul.fallingleaves.data.LeafTypeLoader;
import de.cheaterpaul.fallingleaves.modcompat.SereneSeasons;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.commons.lang3.tuple.Pair;


@Mod(value = FallingLeavesMod.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = FallingLeavesMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FallingLeavesMod {

    public static final String MOD_ID = "fallingleaves";

    public static final ResourceLocation DEFAULT = ResourceLocation.fromNamespaceAndPath(MOD_ID, "default");
    public static final ResourceLocation CONIFER = ResourceLocation.fromNamespaceAndPath(MOD_ID, "conifer");
    public static final ResourceLocation PALMS = ResourceLocation.fromNamespaceAndPath(MOD_ID, "palms");
    public static final ResourceLocation MAHOGANY = ResourceLocation.fromNamespaceAndPath(MOD_ID, "mahogany");
    public static final ResourceLocation MAPLE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "maple");
    public static final ResourceLocation SNOW = ResourceLocation.fromNamespaceAndPath(MOD_ID, "snow");

    public static final ClientConfig CONFIG;
    private static final ModConfigSpec CLIENT_CONFIG_SPEC;

    public FallingLeavesMod(IEventBus modBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        if (SereneSeasons.setup()) {
            modBus.register(SereneSeasons.class);
        }
        container.registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG_SPEC);
    }

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeClient(), new LeafSettingGenerator(event.getGenerator().getPackOutput(), event.getLookupProvider()));
    }

    static {
        SereneSeasons.setup();
        final Pair<ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_CONFIG_SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }
}
