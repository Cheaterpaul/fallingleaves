package de.cheaterpaul.fallingleaves;

import de.cheaterpaul.fallingleaves.init.ClientMod;
import de.cheaterpaul.fallingleaves.init.EventHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(FallingLeavesMod.MOD_ID)
public class FallingLeavesMod {

    public static final String MOD_ID = "fallingleaves";

    public static final Logger LOGGER = LogManager.getLogger();

    public FallingLeavesMod(IEventBus modBus) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientMod.setupClient(modBus);
        } else {
            LOGGER.error("Falling Leaves is a client only mod and should be removed from the mods list");
        }
    }


}
