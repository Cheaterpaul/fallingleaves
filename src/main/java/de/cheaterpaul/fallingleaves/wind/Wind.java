package de.cheaterpaul.fallingleaves.wind;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.config.Config;
import de.cheaterpaul.fallingleaves.wind.math.SmoothNoise;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;

public class Wind {
    private static final float TAU = (float) (2 * Math.PI);

    private final WindState state = new WindState();
    private final ClientLevel level;
    private final SmoothNoise velocityNoise;
    private final SmoothNoise directionTrendNoise;
    private final SmoothNoise directionNoise;

    private float windX;
    private float windZ;

    private boolean active = true;

    public Wind(ClientLevel level) {
        this.level = level;
        this.velocityNoise = new SmoothNoise(2 * 20, 0, (old, random) -> random.nextFloat());
        this.directionTrendNoise = new SmoothNoise(30 * 60 * 20, 0, (old, random) -> random.nextFloat() * TAU);
        this.directionNoise = new SmoothNoise(10 * 20, 0, (old, random) -> (2f * random.nextFloat() - 1f) * TAU / 8f);
    }

    // Optimization: Only update wind calculations every few ticks
    private static final int WIND_UPDATE_INTERVAL = 5; // Update every 5 ticks
    private int tickCounter = 0;

    // Cache for wind gust effects
    private float gustStrength = 0.0f;
    private float gustDuration = 0.0f;
    private float gustFadeTime = 0.0f;

    public void tick() {
        if (checkStatus()) {
            return;
        }

        // Increment tick counter
        tickCounter++;

        // Only update wind state and noise periodically to reduce CPU usage
        boolean updateWind = (tickCounter % WIND_UPDATE_INTERVAL == 0);

        if (updateWind) {
            this.state.tick(this.level);

            this.velocityNoise.tick(this.level.getRandom());
            this.directionTrendNoise.tick(this.level.getRandom());
            this.directionNoise.tick(this.level.getRandom());

            // Calculate base wind values using current wind state
            float stateStrength = this.state.getState().velocityDistribution.sample(this.level.getRandom());
            float noiseModifier = this.velocityNoise.getNoise();
            float strength = stateStrength * (0.8f + 0.4f * noiseModifier); // Apply some variation to the state strength
            float direction = this.directionTrendNoise.getLerp() + this.directionNoise.getNoise();

            // Calculate base wind components
            float baseWindX = strength * Mth.cos(direction);
            float baseWindZ = strength * Mth.sin(direction);

            // Randomly create wind gusts
            if (this.level.getRandom().nextFloat() < 0.05f && this.gustDuration <= 0) {
                // 5% chance of a new gust when no active gust
                this.gustStrength = 0.2f + this.level.getRandom().nextFloat() * 0.3f;
                this.gustDuration = 20 + this.level.getRandom().nextInt(40); // 1-3 seconds
                this.gustFadeTime = 10 + this.level.getRandom().nextInt(20); // 0.5-1.5 seconds fade
            }

            // Apply wind gusts if active
            if (this.gustDuration > 0) {
                float gustFactor;

                // Fade in/out for smoother transitions
                if (this.gustDuration < this.gustFadeTime) {
                    // Fade out
                    gustFactor = this.gustDuration / this.gustFadeTime;
                } else {
                    // Full strength
                    gustFactor = 1.0f;
                }

                // Apply gust to wind components
                this.windX = baseWindX * (1.0f + this.gustStrength * gustFactor);
                this.windZ = baseWindZ * (1.0f + this.gustStrength * gustFactor);

                // Decrease gust duration
                this.gustDuration--;
            } else {
                // No gust, use base wind
                this.windX = baseWindX;
                this.windZ = baseWindZ;
            }
        }
    }

    public void changeWind() {
        this.state.changeWind(this.level);
    }

    public void changeWind(WindState.State state) {
        this.state.changeWind(state);
    }

    public boolean isActive() {
        return this.active;
    }

    private boolean checkStatus() {
        if (!hasWind()) {
            deactivate();
            return true;
        } else if (!this.active) {
            this.active = true;
        }
        return false;
    }

    private boolean hasWind() {
        return Config.CONFIG.wind.hasWind(this.level);
    }

    private void deactivate() {
        if (this.active) {
            this.active = false;
            this.windX = 0;
            this.windZ = 0;
        }
    }

    public float getWindX() {
        return this.windX;
    }

    public float getWindZ() {
        return this.windZ;
    }

}
