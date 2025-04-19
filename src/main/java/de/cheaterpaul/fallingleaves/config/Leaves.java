package de.cheaterpaul.fallingleaves.config;

import de.cheaterpaul.fallingleaves.leaves.ILeavesLevel;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Leaves {
    public final ModConfigSpec.BooleanValue enabled;

    public final Mod mod;
    public final Seasons seasons;

    public Leaves(ModConfigSpec.Builder builder) {
        this.enabled = builder.comment("disable vanilla leaves and enable falling leaves leaves").define("enabled", true);

        builder.push("mod");
        this.mod = new Mod(builder);
        builder.pop();
        builder.push("seasons");
        this.seasons = new Seasons(builder);
        builder.pop();
    }

    public void onLoad(ModConfigEvent event) {
        if (Minecraft.getInstance() != null && Minecraft.getInstance().level != null) {
            de.cheaterpaul.fallingleaves.leaves.Leaves leaves = ((ILeavesLevel) Minecraft.getInstance().level).fallingLeaves$getLeaves();
            leaves.checkSpawner(!this.enabled.get());
        }
    }

    public static class Mod {
        public final ModConfigSpec.IntValue leafSize;
        public final ModConfigSpec.IntValue leafLifespan;
        public final ModConfigSpec.IntValue leafSpawnRate;
        public final ModConfigSpec.DoubleValue minimumFreeSpaceBelow;
        public final ModConfigSpec.BooleanValue particlesDisappearOnGroundContact;

        public Mod(ModConfigSpec.Builder builder) {
            this.leafSize = builder.comment("Modifies the size of the leaves").defineInRange("leafSize", 5, 1, 20);
            this.minimumFreeSpaceBelow = builder.comment("How much room below the leaves block is needed for the leaves to spawn").defineInRange("minimumFreeSpaceBelow", 0.5, 1d, 20d);
            builder.comment("The following config option will affect performance.")
                    .comment("Generally speaking the more leaves that are present will the higher is the performance impact.")
                    .push("performance-relevant");
            this.leafLifespan = builder.comment("Modifies how long it takes for the leaves to disappear.", "In Ticks", "Values over 2000 are not recommend").defineInRange("leafLifespan", 400, 10, Integer.MAX_VALUE);
            this.leafSpawnRate = builder.comment("Modifies the amount of leaves that are spawning.", "Values over 10000 are not recommend").defineInRange("leafSpawnRate", 10, 0, Integer.MAX_VALUE);
            this.particlesDisappearOnGroundContact = builder.comment("Whether the leaves particles should disappear when touching the ground").define("particlesDisappearOnGroundContact", false);
            builder.pop();
        }
    }

    public static class Seasons {
        public final ModConfigSpec.BooleanValue enabled;

        public Seasons(ModConfigSpec.Builder builder) {
            this.enabled = builder.comment("Whether to enable the serene compatibility").define("enabled", true);
        }
    }
}
