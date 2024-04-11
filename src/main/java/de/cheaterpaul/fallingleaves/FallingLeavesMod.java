package de.cheaterpaul.fallingleaves;

import de.cheaterpaul.fallingleaves.init.ClientMod;
import de.cheaterpaul.fallingleaves.init.EventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(FallingLeavesMod.MOD_ID)
public class FallingLeavesMod {

    public static final String MOD_ID = "fallingleaves";

    public static final Logger LOGGER = LogManager.getLogger();

    public FallingLeavesMod() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientMod.setupClient(FMLJavaModLoadingContext.get().getModEventBus());
        } else {
            LOGGER.error("Falling Leaves is a client only mod and should be removed from the mods list");
        }
    }


}
