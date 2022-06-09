package de.cheaterpaul.fallingleaves.init;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.particle.FallingConiferLeafParticle;
import de.cheaterpaul.fallingleaves.particle.FallingLeafParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class Leaves {
    public static final FallingLeafParticle.DefaultFactory falling_leaf = new FallingLeafParticle.DefaultFactory();
    public static final FallingLeafParticle.DefaultFactory falling_leaf_conifer = new FallingConiferLeafParticle.DefaultFactory();
}
