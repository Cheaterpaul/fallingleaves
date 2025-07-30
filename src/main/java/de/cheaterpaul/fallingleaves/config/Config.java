package de.cheaterpaul.fallingleaves.config;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

@EventBusSubscriber(modid = FallingLeavesMod.MODID, value = Dist.CLIENT)
public class Config {

    public static final ModConfigSpec CONFIG_SPEC;
    public static final Config CONFIG;

    public final Leaves leaves;
    public final Wind wind;
    public final Snow snow;

    public Config(ModConfigSpec.Builder builder) {
        builder.translation(FallingLeavesMod.MODID + ".configuration.leaves")
                .push("leaves");
        this.leaves = new Leaves(builder);
        builder.pop();
        builder.translation(FallingLeavesMod.MODID + ".configuration.wind")
                .push("wind");
        this.wind = new Wind(builder);
        builder.pop();
        builder.translation(FallingLeavesMod.MODID + ".configuration.snow")
                .push("snow");
        this.snow = new Snow(builder);
        builder.pop();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        CONFIG.wind.onLoad(event);
        CONFIG.leaves.onLoad(event);
    }


    static {
        Pair<Config, ModConfigSpec> configure = new ModConfigSpec.Builder().configure(Config::new);
        CONFIG = configure.getLeft();
        CONFIG_SPEC = configure.getRight();
    }

}
