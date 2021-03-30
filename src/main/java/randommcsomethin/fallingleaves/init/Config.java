package randommcsomethin.fallingleaves.init;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import randommcsomethin.fallingleaves.config.*;

public class Config {

    public static final ClientConfig CONFIG;
    private static final ForgeConfigSpec clientSpec;

    public static final TreeValueLoader LEAFSETTINGS = new TreeValueLoader();

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        clientSpec = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    public static void registerConfigs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
    }

    @SubscribeEvent
    public void onReloadListenerEvent(AddReloadListenerEvent event) {
        event.addListener(LEAFSETTINGS);
    }

}