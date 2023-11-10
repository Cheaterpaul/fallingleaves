package de.cheaterpaul.fallingleaves.init;

import de.cheaterpaul.fallingleaves.config.ClientConfig;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class FallingLeavesConfig {

    public static final ClientConfig CONFIG;
    private static final ModConfigSpec clientSpec;

    static {
        final Pair<ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        clientSpec = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    public static void registerConfigs() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
    }
}