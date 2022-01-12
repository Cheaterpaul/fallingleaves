package de.cheaterpaul.fallingleaves.init;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public interface IParticleEngine {

    <T extends ParticleOptions> void registerModded(ParticleType<T> p_107379_, ParticleEngine.SpriteParticleRegistration<T> p_107380_);
}
