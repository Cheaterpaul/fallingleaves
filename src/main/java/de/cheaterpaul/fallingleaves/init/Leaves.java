package de.cheaterpaul.fallingleaves.init;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.particle.FallingConiferLeafParticle;
import de.cheaterpaul.fallingleaves.particle.FallingLeafParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ResourceLocation;

public class Leaves {
    public static boolean ADDED = false;
    public static final BasicParticleType falling_leaf = (BasicParticleType) new BasicParticleType(false).setRegistryName(modId("falling_leaf"));
    public static final BasicParticleType falling_leaf_conifer = (BasicParticleType) new BasicParticleType(false).setRegistryName(modId("falling_leaf_conifer"));

    public static ResourceLocation modId(String name) {
        return new ResourceLocation(FallingLeavesMod.MOD_ID, name);
    }

    public static void registerParticles() {
        Minecraft.getInstance().particleEngine.register(falling_leaf, FallingLeafParticle.DefaultFactory::new);
        Minecraft.getInstance().particleEngine.register(falling_leaf_conifer, FallingConiferLeafParticle.DefaultFactory::new);
        ADDED = true;
    }
}
