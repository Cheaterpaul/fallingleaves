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
        leafSize = builder.defineInRange("leafSize", 4, 1, 20);
        leafLifespan = builder.defineInRange("leafLifespan", 200, 100, 2000);
        leafSpawnRate = builder.defineInRange("leafSpawnRate", 10, 0, 10000);
        coniferLeafSpawnRate = builder.defineInRange("coniferLeafSpawnRate", 2, 0, 10000);
        dropFromPlayerPlacedBlocks = builder.define("dropFromPlayerPlacedBlocks", true);
        minimumFreeSpaceBelow = builder.defineInRange("minimumFreeSpaceBelow", 1, 1, 20);
    }

}