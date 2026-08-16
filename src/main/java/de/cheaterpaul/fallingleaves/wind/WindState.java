package de.cheaterpaul.fallingleaves.wind;

import com.mojang.logging.LogUtils;
import de.cheaterpaul.fallingleaves.wind.math.ITriangularDistribution;
import de.cheaterpaul.fallingleaves.wind.math.TriangularDistribution;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNullByDefault;
import org.slf4j.Logger;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class WindState {

    private static final Logger LOGGER = LogUtils.getLogger();

    private boolean wasRaining;
    private boolean wasThundering;

    private State state = State.NO_WIND;
    private int duration;

    public void tick(ClientLevel level) {
        --this.duration;

        boolean isRaining = level.isRaining();
        boolean isThundering = level.isThundering();
        boolean weatherChanged = this.wasRaining != isRaining || this.wasThundering != isThundering;

        if (weatherChanged || duration <= 0) {
            if (isThundering) {
                this.state = StateGroup.STORM.getRandomState(level.getRandom());
            } else {
                // windy and stormy when raining, calm and windy otherwise
                changeWind(level);
            }

            this.duration = 6 * 60 * 20; // change state every 6 minutes
            LOGGER.trace("new wind state {}", state);
        }

        this.wasRaining = isRaining;
        this.wasThundering = isThundering;
    }

    public void changeWind(ClientLevel level) {
        int index = level.getRandom().nextInt(2);
        this.state = StateGroup.values()[index].getRandomState(level.getRandom());
    }

    public void changeWind(WindState.State state) {
        this.state = state;
        this.duration = 6 * 60 * 20; // change state every 6 minutes
    }

    public State getState() {
        return this.state;
    }

    public enum State {
        NO_WIND(),
        CALM(0.05f, 0.05f, 0.2f),
        LIGHT(0.1f, 0.1f, 0.5f),
        WINDY(0.15f, 0.3f, 0.7f),
        VERY_WINDY(0.2f, 0.5f, 0.9f),
        STORMY(0.25f, 0.7f, 1.1f),
        HURRICANE(0.3f, 0.9f, 1.3f);

        public final ITriangularDistribution velocityDistribution;

        State() {
            this.velocityDistribution = random -> 0;
        }

        State(float minSpeed, float likelySpeed, float maxSpeed) {
            this.velocityDistribution = new TriangularDistribution(minSpeed, maxSpeed, likelySpeed);
        }
    }

    public enum StateGroup {
        CALM(State.NO_WIND, State.CALM, State.LIGHT),
        WIND(State.LIGHT, State.WINDY, State.VERY_WINDY),
        STORM(State.VERY_WINDY, State.STORMY, State.HURRICANE);

        final List<State> states;

        StateGroup(State... states) {
            this.states = List.of(states);
        }

        public List<State> getStates() {
            return states;
        }

        public State getRandomState(RandomSource random) {
            return this.states.get(random.nextInt(this.states.size()));
        }
    }
}
