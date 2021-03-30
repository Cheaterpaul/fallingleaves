package randommcsomethin.fallingleaves;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import randommcsomethin.fallingleaves.data.LeafSettingGenerator;
import randommcsomethin.fallingleaves.init.Config;
import randommcsomethin.fallingleaves.init.Leaves;

@Mod("fallingleaves")
public class FallingLeavesMod {

    /** The mod's unique identifier, used to avoid mod conflicts in the Registry and config files */
    public static final String MOD_ID = "fallingleaves";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public FallingLeavesMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Config.registerConfigs();
        MinecraftForge.EVENT_BUS.register(new Leaves());
        bus.addListener(this::gatherData);
        bus.addListener(this::clientSetup);
    }

    private void gatherData(final GatherDataEvent event) {
        if (event.includeClient()) {
            event.getGenerator().addProvider(new LeafSettingGenerator(event.getGenerator()));
        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        Leaves.registerParticles();
    }

}
