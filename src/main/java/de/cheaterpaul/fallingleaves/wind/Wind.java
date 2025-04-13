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
        this.velocityNoise = new SmoothNoise(2 * 20, 0, (old, random) -> state.getState().velocityDistribution.sample(random));
        this.directionTrendNoise = new SmoothNoise(30 * 60 * 20, 0, (old, random) -> random.nextFloat() * TAU);
        this.directionNoise = new SmoothNoise(10 * 20, 0, (old, random) -> (2f * random.nextFloat() - 1f) * TAU / 8f);
    }

    public void tick() {
        if (checkStatus()) {
            return;
        }
        this.state.tick(this.level);

        this.velocityNoise.tick(this.level.getRandom());
        this.directionTrendNoise.tick(this.level.getRandom());
        this.directionNoise.tick(this.level.getRandom());

        float strength = this.velocityNoise.getNoise();
        float direction = this.directionTrendNoise.getLerp() + this.directionNoise.getNoise();

        this.windX = strength * Mth.cos(direction);
        this.windZ = strength * Mth.sin(direction);
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
