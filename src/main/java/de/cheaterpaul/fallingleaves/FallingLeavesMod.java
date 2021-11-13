package de.cheaterpaul.fallingleaves;

import de.cheaterpaul.fallingleaves.data.LeafSettingGenerator;
import de.cheaterpaul.fallingleaves.init.EventHandler;
import de.cheaterpaul.fallingleaves.init.FallingLeavesConfig;
import de.cheaterpaul.fallingleaves.init.Leaves;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("fallingleaves")
public class FallingLeavesMod {

    public static final String MOD_ID = "fallingleaves";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public FallingLeavesMod() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FallingLeavesConfig.registerConfigs();
            bus.addListener(this::gatherData);
            bus.addListener(this::registerParticles);
        });
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> EventHandler::new);
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> LOGGER.warn("Falling Leaves is a client only mod and should be removed from the mods list"));
    }

    private void gatherData(final GatherDataEvent event) {
        if (event.includeClient()) {
            event.getGenerator().addProvider(new LeafSettingGenerator(event.getGenerator()));
        }
    }

    private void registerParticles(ParticleFactoryRegisterEvent event) {
        Leaves.registerParticles();
    }
}
