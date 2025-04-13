package de.cheaterpaul.fallingleaves.wind.math;

import net.minecraft.util.RandomSource;

@FunctionalInterface
public interface ITriangularDistribution {

    float sample(RandomSource random);
}
