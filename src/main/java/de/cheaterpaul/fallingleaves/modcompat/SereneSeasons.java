package de.cheaterpaul.fallingleaves.modcompat;

import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class SereneSeasons {

    private static SereneSeasonsConfig config;
    private static boolean isEnabled;

    public static boolean setup() {
        return isEnabled = ModList.get().isLoaded("sereneseasons");
    }

    public static float getModifier(Level level) {
        return config == null ? 1 : config.getModifier(level);
    }

    public static void registerConfig(ForgeConfigSpec.Builder builder) {
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
