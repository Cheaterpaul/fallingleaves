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
        leafSize = builder.comment("modifies the size of the leaves").defineInRange("leafSize", 4, 1, 20);
        leafLifespan = builder.comment("modifies how long it takes for the leaves to disappear").defineInRange("leafLifespan", 200, 100, 2000);
        leafSpawnRate = builder.comment("modifies the amount of leaves that are spawning").defineInRange("leafSpawnRate", 10, 0, 10000);
        coniferLeafSpawnRate = builder.comment("modifier the amount of leaves are spawning from conifer trees").defineInRange("coniferLeafSpawnRate", 2, 0, 10000);
        dropFromPlayerPlacedBlocks = builder.comment("weather player placed blocks should also drop leaves").define("dropFromPlayerPlacedBlocks", true);
        minimumFreeSpaceBelow = builder.comment("how much room below the leaves block is needed for the leaves to spawn").defineInRange("minimumFreeSpaceBelow", 1, 1, 20);
    }

}