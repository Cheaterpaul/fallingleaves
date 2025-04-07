package de.cheaterpaul.fallingleaves;

import com.mojang.logging.LogUtils;
import de.cheaterpaul.fallingleaves.config.Config;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

@Mod(value = FallingLeavesMod.MODID, dist = Dist.CLIENT)
public class FallingLeavesMod {
    public static final String MODID = "fallingleaves";
    public static FallingLeavesMod INSTANCE;
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Config CONFIG;
    private static final ModConfigSpec CONFIG_SPEC;

    public FallingLeavesMod(IEventBus modEventBus, ModContainer modContainer) {
        INSTANCE = this;
        modContainer.registerConfig(ModConfig.Type.CLIENT, CONFIG_SPEC);
        modEventBus.addListener(CONFIG::onLoad);
    }

    static {
        Pair<Config, ModConfigSpec> configure = new ModConfigSpec.Builder().configure(Config::new);
        CONFIG = configure.getLeft();
        CONFIG_SPEC = configure.getRight();
    }

}
