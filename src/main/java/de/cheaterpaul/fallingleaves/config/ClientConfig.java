package de.cheaterpaul.fallingleaves.config;

import com.google.common.collect.Lists;
//import de.cheaterpaul.fallingleaves.modcompat.SereneSeasons;
import de.cheaterpaul.fallingleaves.modcompat.SereneSeasons;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ClientConfig {

    public final ForgeConfigSpec.IntValue leafSize;
    public final ForgeConfigSpec.IntValue leafLifespan;
    public final ForgeConfigSpec.IntValue leafSpawnRate;
    public final ForgeConfigSpec.IntValue coniferLeafSpawnRate;
    public final ForgeConfigSpec.BooleanValue dropFromPlayerPlacedBlocks;
    public final ForgeConfigSpec.IntValue minimumFreeSpaceBelow;
    public final ForgeConfigSpec.BooleanValue disableWind;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> windlessDimension;

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        leafSize = builder.comment("modifies the size of the leaves").defineInRange("leafSize", 4, 1, 20);
        leafLifespan = builder.comment("modifies how long it takes for the leaves to disappear", "Values over 2000 are not recommend").defineInRange("leafLifespan", 200, 100, Integer.MAX_VALUE);
        leafSpawnRate = builder.comment("modifies the amount of leaves that are spawning", "Values over 10000 are not recommend").defineInRange("leafSpawnRate", 10, 0, Integer.MAX_VALUE);
        coniferLeafSpawnRate = builder.comment("modifier the amount of leaves are spawning from conifer trees", "Values over 10000 are not recommend").defineInRange("coniferLeafSpawnRate", 2, 0, Integer.MAX_VALUE);
        dropFromPlayerPlacedBlocks = builder.comment("whether player placed blocks should also drop leaves").define("dropFromPlayerPlacedBlocks", true);
        minimumFreeSpaceBelow = builder.comment("how much room below the leaves block is needed for the leaves to spawn").defineInRange("minimumFreeSpaceBelow", 1, 1, 20);
        disableWind = builder.comment("disable wind effects").define("disableWind", false);
        windlessDimension = builder.comment("windless dimensions").defineList("windlessDimension", Lists.newArrayList(BuiltinDimensionTypes.NETHER.location().toString(), BuiltinDimensionTypes.END.location().toString()), string -> string instanceof String && ResourceLocation.tryParse(((String) string)) != null);
        SereneSeasons.registerConfig(builder);
    }

}