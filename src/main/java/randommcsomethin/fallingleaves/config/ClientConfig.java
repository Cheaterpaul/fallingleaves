package randommcsomethin.fallingleaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.function.Consumer;

@SuppressWarnings({"FieldMayBeFinal", "CanBeFinal"})
public class ClientConfig {

    public final ForgeConfigSpec.IntValue version;

    public final ForgeConfigSpec.BooleanValue displayDebugData;

    public final ForgeConfigSpec.IntValue leafSize;

    public final ForgeConfigSpec.IntValue leafLifespan;

    public final  ForgeConfigSpec.IntValue leafSpawnRate;

    public final  ForgeConfigSpec.IntValue coniferLeafSpawnRate;
    public final ForgeConfigSpec.BooleanValue dropFromPlayerPlacedBlocks;
    public final ForgeConfigSpec.IntValue minimumFreeSpaceBelow;

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        version =builder.defineInRange("version",1,1,1);
        displayDebugData = builder.define("displayDebugData", false);
        leafSize = builder.defineInRange("leafSize", 5,1,10);
        leafLifespan = builder.defineInRange("leafLifespan", 200,100,600);
        leafSpawnRate = builder.comment("vale/10/75").defineInRange("leafSpawnRate", 0,10,20);
        coniferLeafSpawnRate = builder.comment("value/10/75").defineInRange("coniferLeafSpawnRate", 0, 0, 20);
        dropFromPlayerPlacedBlocks = builder.define("dropFromPlayerPlacedBlocks", true);
        minimumFreeSpaceBelow = builder.defineInRange("minimumFreeSpaceBelow", 1,1,20);
    }

}