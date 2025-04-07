package de.cheaterpaul.fallingleaves.wind;

import com.mojang.logging.LogUtils;
import de.cheaterpaul.fallingleaves.wind.math.TriangularDistribution;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNullByDefault;
import org.slf4j.Logger;

@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class WindState {

    private static final Logger LOGGER = LogUtils.getLogger();

    private boolean wasRaining;
    private boolean wasThundering;

    private State state = State.CALM;
    private int duration;

    public void tick(ClientLevel level) {
        --this.duration;

        boolean isRaining = level.getLevelData().isRaining();
        boolean isThundering = level.isThundering();
        boolean weatherChanged = wasRaining != isRaining || wasThundering != isThundering;

        if (weatherChanged || duration <= 0) {
            if (isThundering) {
                state = State.STORMY;
            } else {
                // windy and stormy when raining, calm and windy otherwise
                int index = level.random.nextInt(2);
                state = State.values()[(isRaining ? index + 1 : index)];
            }

            duration = 6 * 60 * 20; // change state every 6 minutes
            LOGGER.debug("new wind state {}", state);
        }

        wasRaining = isRaining;
        wasThundering = isThundering;
    }

    public State getState() {
        return state;
    }

    private boolean hasWind(ClientLevel level) {
        ResourceLocation location = level.dimension().location();
        //TODO check config
        return true;
    }

    public enum State {
        CALM(0.05f, 0.05f, 0.2f),
        WINDY(0.05f, 0.3f, 0.7f),
        STORMY(0.05f, 0.6f, 1.1f);

        public final TriangularDistribution velocityDistribution;

        State(@SuppressWarnings("SameParameterValue") float minSpeed, float likelySpeed, float maxSpeed) {
            this.velocityDistribution = new TriangularDistribution(minSpeed, maxSpeed, likelySpeed);
        }
    }
}
