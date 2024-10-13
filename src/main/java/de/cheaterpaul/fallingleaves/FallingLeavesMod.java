package de.cheaterpaul.fallingleaves;

import de.cheaterpaul.fallingleaves.init.ClientMod;
import de.cheaterpaul.fallingleaves.init.EventHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(value = FallingLeavesMod.MOD_ID, dist = Dist.CLIENT)
public class FallingLeavesMod {

    public static final String MOD_ID = "fallingleaves";

    public FallingLeavesMod(IEventBus modBus, ModContainer container) {
        ClientMod.setupClient(modBus);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }


}
