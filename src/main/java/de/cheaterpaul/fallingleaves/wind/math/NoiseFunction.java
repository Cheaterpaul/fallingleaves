package de.cheaterpaul.fallingleaves.wind.math;

import net.minecraft.util.RandomSource;

@FunctionalInterface
public interface NoiseFunction {
    float apply(float previous, RandomSource random);
}
