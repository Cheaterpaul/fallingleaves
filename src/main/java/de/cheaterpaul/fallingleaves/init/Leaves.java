package de.cheaterpaul.fallingleaves.init;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.particle.FallingConiferLeafParticle;
import de.cheaterpaul.fallingleaves.particle.FallingLeafParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Leaves {

    public static final BasicParticleType falling_leaf = new BasicParticleType(false);
    public static final BasicParticleType falling_leaf_conifer = new BasicParticleType(false);


    @SubscribeEvent
    public void onRegisterParticles(RegistryEvent.Register<ParticleType<?>> event) {
        event.getRegistry().register(falling_leaf.setRegistryName(modId("falling_leaf")));
        event.getRegistry().register(falling_leaf_conifer.setRegistryName(modId("falling_leaf_conifer")));
    }

    public static ResourceLocation modId(String name) {
        return new ResourceLocation(FallingLeavesMod.MOD_ID, name);
    }

    public static void registerParticles() {
        Minecraft.getInstance().particleEngine.register(falling_leaf, FallingLeafParticle.DefaultFactory::new);
        Minecraft.getInstance().particleEngine.register(falling_leaf_conifer, FallingConiferLeafParticle.DefaultFactory::new);
    }
}
