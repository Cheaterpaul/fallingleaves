package de.cheaterpaul.fallingleaves.wind.math;


import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNullByDefault;

@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class SmoothNoise {
    protected final int tickInterval;
    protected final NoiseFunction nextNoise;
    protected float leftNoise;
    protected float rightNoise;
    protected int ticks = 0;
    protected float t;

    public SmoothNoise(int tickInterval, float initial, NoiseFunction nextNoise) {
        if (tickInterval < 1)
            throw new IllegalArgumentException(String.format("tickInterval %d < 1", tickInterval));

        this.tickInterval = tickInterval;
        this.nextNoise = nextNoise;
        this.leftNoise = 0;
        this.rightNoise = initial;
    }

    /**
     * Smoothly goes from 0 to 1 when t increases from 0 and 1. Defined for t in [0, 1].
     */
    public static float smoothStep(float t) {
        return t * t * (3 - 2 * t);
    }

    public void tick(RandomSource random) {
        this.ticks++;

        if (this.ticks == this.tickInterval) {
            this.ticks = 0;
            this.leftNoise = this.rightNoise;
            this.rightNoise = this.nextNoise.apply(this.leftNoise, random);
        }

        this.t = this.ticks / (float) this.tickInterval; // in [0, 1)
    }

    public float getLeftNoise() {
        return this.leftNoise;
    }

    public float getRightNoise() {
        return this.rightNoise;
    }

    /**
     * Linear interpolation between left and right noise values
     */
    public float getLerp() {
        return this.leftNoise + this.t * (this.rightNoise - this.leftNoise);
    }

    /**
     * Smooth interpolation between left and right noise values
     */
    public float getNoise() {
        return this.leftNoise + smoothStep(this.t) * (this.rightNoise - this.leftNoise);
    }
}
