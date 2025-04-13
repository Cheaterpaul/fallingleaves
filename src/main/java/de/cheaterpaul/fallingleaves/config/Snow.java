package de.cheaterpaul.fallingleaves.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Snow {
    public final ModConfigSpec.BooleanValue enabled;

    public Snow(ModConfigSpec.Builder builder) {
        this.enabled = builder.comment("Whether to enable the snow").define("enabled", true);
    }
}
