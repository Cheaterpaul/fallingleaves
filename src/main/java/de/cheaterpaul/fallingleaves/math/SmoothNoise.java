package de.cheaterpaul.fallingleaves.math;

import javax.annotation.Nonnull;

public class SmoothNoise {
    protected final int tickInterval;
    protected final FloatFunction nextNoise;
    protected float leftNoise;
    protected float rightNoise;
    protected int ticks = 0;
    protected float t;

    public SmoothNoise(int tickInterval, float initial, @Nonnull FloatFunction nextNoise) {
        if (tickInterval < 1)
            throw new IllegalArgumentException(String.format("tickInterval %d < 1", tickInterval));

        this.tickInterval = tickInterval;
        this.nextNoise = nextNoise;
        this.leftNoise = initial;
        this.rightNoise = nextNoise.apply(leftNoise);
    }

    /**
     * Smoothly goes from 0 to 1 when t increases from 0 and 1. Defined for t in [0, 1].
     */
    public static float smoothstep(float t) {
        return t * t * (3 - 2 * t);
    }

    public void tick() {
        ticks++;

        if (ticks == tickInterval) {
            ticks = 0;
            leftNoise = rightNoise;
            rightNoise = nextNoise.apply(leftNoise);
        }

        t = ticks / (float) tickInterval; // in [0, 1)
    }

    public float getLeftNoise() {
        return leftNoise;
    }

    public float getRightNoise() {
        return rightNoise;
    }

    /**
     * Linear interpolation between left and right noise values
     */
    public float getLerp() {
        return leftNoise + t * (rightNoise - leftNoise);
    }

    /**
     * Smooth interpolation between left and right noise values
     */
    public float getNoise() {
        return leftNoise + smoothstep(t) * (rightNoise - leftNoise);
    }
}
