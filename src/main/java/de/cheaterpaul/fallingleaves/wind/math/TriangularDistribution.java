package de.cheaterpaul.fallingleaves.wind.math;

import net.minecraft.util.RandomSource;

public class TriangularDistribution {
    public final float a, b, c;
    protected final float f;

    public TriangularDistribution(float a, float b, float c) {
        if (!(a < b) || !(a <= c && c <= b))
            throw new IllegalArgumentException(String.format("not %f <= %f <= %f", a, b, c));

        this.a = a;
        this.b = b;
        this.c = c;
        this.f = (c - a) / (b - a);
    }

    public float sample(RandomSource random) {
        float u = random.nextFloat();
        if (u < f) return a + (float) Math.sqrt(u * (b - a) * (c - a));
        else return b - (float) Math.sqrt((1f - u) * (b - a) * (b - c));
    }
}
