package de.cheaterpaul.fallingleaves;

import de.cheaterpaul.fallingleaves.init.ClientMod;
import de.cheaterpaul.fallingleaves.init.EventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("fallingleaves")
public class FallingLeavesMod {

    public static final String MOD_ID = "fallingleaves";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public FallingLeavesMod() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "", (incoming, isNetwork) -> true));
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientMod::setupClient);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> EventHandler::new);
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> LOGGER.warn("Falling Leaves is a client only mod and should be removed from the mods list"));
    }


}
