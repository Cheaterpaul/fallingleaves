package de.cheaterpaul.fallingleaves.config;

import com.google.common.collect.Lists;
//import de.cheaterpaul.fallingleaves.modcompat.SereneSeasons;
import de.cheaterpaul.fallingleaves.modcompat.SereneSeasons;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;

public class ClientConfig {

    public final ModConfigSpec.IntValue leafSize;
    public final ModConfigSpec.IntValue leafLifespan;
    public final ModConfigSpec.IntValue leafSpawnRate;
    public final ModConfigSpec.IntValue snowSpawnRate;
    public final ModConfigSpec.IntValue coniferLeafSpawnRate;
    public final ModConfigSpec.BooleanValue dropFromPlayerPlacedBlocks;
    public final ModConfigSpec.IntValue maxDecayingLeaves;
    public final ModConfigSpec.IntValue minimumFreeSpaceBelow;
    public final ModConfigSpec.BooleanValue disableWind;
    public final ModConfigSpec.ConfigValue<List<? extends String>> windlessDimension;
    public final ModConfigSpec.BooleanValue disableSeasonalModifier;
    public final ModConfigSpec.BooleanValue disableSnow;
    public final ModConfigSpec.IntValue maxDecayingSnowParticles;

    public ClientConfig(ModConfigSpec.Builder builder) {
        builder.push("leaf_settings");
        leafSize = builder.comment("Modifies the size of the leaves").defineInRange("leafSize", 4, 1, 20);
        leafLifespan = builder.comment("Modifies how long it takes for the leaves to disappear.", "In Ticks", "Values over 2000 are not recommend").defineInRange("leafLifespan", 200, 100, Integer.MAX_VALUE);
        leafSpawnRate = builder.comment("Modifies the amount of leaves that are spawning.", "Values over 10000 are not recommend").defineInRange("leafSpawnRate", 10, 0, Integer.MAX_VALUE);
        coniferLeafSpawnRate = builder.comment("Modifies the amount of leaves are spawning from conifer trees", "Values over 10000 are not recommend").defineInRange("coniferLeafSpawnRate", 2, 0, Integer.MAX_VALUE);
        dropFromPlayerPlacedBlocks = builder.comment("Whether player placed blocks should also drop leaves").define("dropFromPlayerPlacedBlocks", true);
        minimumFreeSpaceBelow = builder.comment("How much room below the leaves block is needed for the leaves to spawn").defineInRange("minimumFreeSpaceBelow", 1, 1, 20);
        maxDecayingLeaves = builder.comment("How many leaves should spawn when a leaves block is decaying", "This is the maximum amount, but they are binomial distributed.", "This only works in singleplayer").defineInRange("decayingLeaves", 10, 1, 1000);
        builder.pop();
        builder.push("wind");
        disableWind = builder.comment("Disable wind effects").define("disableWind", false);
        windlessDimension = builder.comment("Windless dimensions", "By level registry name").defineList("windlessDimension", Lists.newArrayList(Level.NETHER.location().toString(), Level.END.location().toString()), () -> "namespace:path", obj -> checkRegistryObjectExistence(Registries.DIMENSION, obj));
        builder.pop();
        builder.push("snow");
        disableSnow = builder.comment("Disable snow particles").define("disableSnow", false);
        snowSpawnRate = builder.comment("Modifies the amount of snow that is spawning.", "Values over 10000 are not recommend").defineInRange("snowSpawnRate", 10, 0, Integer.MAX_VALUE);
        maxDecayingSnowParticles = builder.comment("How many snow particles should spawn when a snow block is decaying", "This is the maximum amount, but they are binomial distributed.", "This only works in singleplayer").defineInRange("decayingSnow", 40, 1, 1000);
        builder.pop();
        builder.push("serene_seasons");
        disableSeasonalModifier = builder.comment("Disable the seasonal modifier when serene season is installed").define("disableSeasonalModifier", false);
        SereneSeasons.registerConfig(builder);
        builder.pop();
    }

    private static boolean checkRegistryObjectExistence(ResourceKey<? extends Registry<?>> key, Object obj) {
        if (obj instanceof String string) {
            ResourceLocation id = ResourceLocation.tryParse(string);
            if (id != null) {
                if (ServerLifecycleHooks.getCurrentServer() != null) {
                    return ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(key).containsKey(id);
                } else {
                    return true;
                }
            }
        }
        return false;
    }

}