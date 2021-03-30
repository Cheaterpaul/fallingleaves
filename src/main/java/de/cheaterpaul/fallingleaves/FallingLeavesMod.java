package de.cheaterpaul.fallingleaves;

import de.cheaterpaul.fallingleaves.data.LeafSettingGenerator;
import de.cheaterpaul.fallingleaves.init.Config;
import de.cheaterpaul.fallingleaves.init.EventHandler;
import de.cheaterpaul.fallingleaves.init.Leaves;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("fallingleaves")
public class FallingLeavesMod {

    public static final String MOD_ID = "fallingleaves";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public FallingLeavesMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Config.registerConfigs();
        bus.register(new Leaves());
        bus.addListener(this::gatherData);
        bus.addListener(this::setup);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> EventHandler::new);
    }

    private void gatherData(final GatherDataEvent event) {
        if (event.includeClient()) {
            event.getGenerator().addProvider(new LeafSettingGenerator(event.getGenerator()));
        }
    }

    private void setup(FMLCommonSetupEvent event) {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> Leaves::registerParticles);
    }
}
