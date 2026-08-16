package de.cheaterpaul.fallingleaves.event.leaves;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.leaves.mod.FallingLeafParticle;
import net.minecraft.client.particle.QuadParticleGroup;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleGroupsEvent;

@EventBusSubscriber(modid = FallingLeavesMod.MODID, value = Dist.CLIENT)
public class EventHandler {

    @SubscribeEvent
    public static void registerParticles(RegisterParticleGroupsEvent event) {
        event.register(FallingLeafParticle.LEAVES_SHEET, x -> new QuadParticleGroup(x, FallingLeafParticle.LEAVES_SHEET));
    }
}
