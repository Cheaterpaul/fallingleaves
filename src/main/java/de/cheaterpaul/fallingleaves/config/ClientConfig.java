package de.cheaterpaul.fallingleaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
public class ClientConfig {

    public final ForgeConfigSpec.IntValue leafSize;
    public final ForgeConfigSpec.IntValue leafLifespan;
    public final ForgeConfigSpec.IntValue leafSpawnRate;
    public final ForgeConfigSpec.IntValue coniferLeafSpawnRate;
    public final ForgeConfigSpec.BooleanValue dropFromPlayerPlacedBlocks;
    public final ForgeConfigSpec.IntValue minimumFreeSpaceBelow;

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        leafSize = builder.defineInRange("leafSize", 5, 1, 10);
        leafLifespan = builder.defineInRange("leafLifespan", 200, 100, 1200);
        leafSpawnRate = builder.comment("vale/10/75").defineInRange("leafSpawnRate", 10, 0, 100);
        coniferLeafSpawnRate = builder.comment("value/10/75").defineInRange("coniferLeafSpawnRate", 0, 0, 100);
        dropFromPlayerPlacedBlocks = builder.define("dropFromPlayerPlacedBlocks", true);
        minimumFreeSpaceBelow = builder.defineInRange("minimumFreeSpaceBelow", 1, 1, 20);
    }

}