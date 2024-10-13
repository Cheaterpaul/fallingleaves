package de.cheaterpaul.fallingleaves.modcompat;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.config.ClientConfig;
import de.cheaterpaul.fallingleaves.init.FallingLeavesConfig;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class SereneSeasons {

    private static SereneSeasonsConfig config;
    private static boolean isEnabled;

    public static boolean setup() {
        return isEnabled = ModList.get().isLoaded("sereneseasons");
    }

    public static float getModifier(Level level) {
        if (FallingLeavesConfig.CONFIG.disableSeasonalModifier.get() || config == null) {
            return 1;
        }
        return config.getModifier(level);
    }

    public static void registerConfig(ModConfigSpec.Builder builder) {
        if (isEnabled) {
            config = new SereneSeasonsConfig(builder);
        }
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        if (config != null) {
            config.updateCache();
        }
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Reloading event) {
        if (config != null) {
            config.updateCache();
        }
    }
}
