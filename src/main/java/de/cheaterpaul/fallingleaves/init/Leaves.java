package de.cheaterpaul.fallingleaves.init;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.particle.FallingConiferLeafParticle;
import de.cheaterpaul.fallingleaves.particle.FallingLeafParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;

public class Leaves {
    public static boolean ADDED = false;
    public static final SimpleParticleType falling_leaf = (SimpleParticleType) new SimpleParticleType(false).setRegistryName(modId("falling_leaf"));
    public static final SimpleParticleType falling_leaf_conifer = (SimpleParticleType) new SimpleParticleType(false).setRegistryName(modId("falling_leaf_conifer"));

    public static ResourceLocation modId(String name) {
        return new ResourceLocation(FallingLeavesMod.MOD_ID, name);
    }

    public static void registerParticles() {
        Minecraft.getInstance().particleEngine.register(falling_leaf, FallingLeafParticle.DefaultFactory::new);
        Minecraft.getInstance().particleEngine.register(falling_leaf_conifer, FallingConiferLeafParticle.DefaultFactory::new);
        ADDED = true;
    }
}
