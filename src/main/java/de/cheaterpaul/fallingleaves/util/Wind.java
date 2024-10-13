package de.cheaterpaul.fallingleaves.util;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.math.SmoothNoise;
import de.cheaterpaul.fallingleaves.math.TriangularDistribution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class Wind {
    private static final Logger LOGGER = LogManager.getLogger();
    public static float windX;
    public static float windZ;
    protected static final Random rng = new Random();
    protected static final float TAU = (float) (2 * Math.PI);
    protected static SmoothNoise velocityNoise;
    protected static SmoothNoise directionTrendNoise;
    protected static SmoothNoise directionNoise;
    protected static boolean wasRaining;
    protected static boolean wasThundering;
    protected static State state;
    protected static State originalState;
    protected static int stateDuration; // ticks

    public static void debug() {
        state = State.values()[(state.ordinal() + 1) % State.values().length];
        ChatComponent chatHud = Minecraft.getInstance().gui.getChat();
        chatHud.addMessage(Component.literal("set wind state to " + state));
    }

    public static void init() {
        LOGGER.debug("Wind.init");

        wasRaining = false;
        wasThundering = false;
        state = State.CALM;
        stateDuration = 0;

        windX = windZ = 0;

        velocityNoise = new SmoothNoise(2 * 20, 0, (old) -> {
            return state.velocityDistribution.sample();
        });
        directionTrendNoise = new SmoothNoise(30 * 60 * 20, rng.nextFloat() * TAU, (old) -> {
            return rng.nextFloat() * TAU;
        });
        directionNoise = new SmoothNoise(10 * 20, 0, (old) -> {
            return (2f * rng.nextFloat() - 1f) * TAU / 8f;
        });
    }

    protected static void tickState(ClientLevel world) {
        --stateDuration;

        ResourceLocation dimension = world.dimension().location();
        if (FallingLeavesMod.CONFIG.disableWind.get() || FallingLeavesMod.CONFIG.windlessDimension.get().contains(dimension.toString())) {
            // override state to calm when there is no wind
            originalState = state;
            state = State.CALM;
            return;
        }

        // restore overridden state
        if (originalState != null) {
            state = originalState;
            originalState = null;
        }

        boolean isRaining = world.getLevelData().isRaining();
        boolean isThundering = world.isThundering();
        boolean weatherChanged = wasRaining != isRaining || wasThundering != isThundering;

        if (weatherChanged || stateDuration <= 0) {
            if (isThundering) {
                state = State.STORMY;
            } else {
                // windy and stormy when raining, calm and windy otherwise
                int index = rng.nextInt(2);
                state = State.values()[(isRaining ? index + 1 : index)];
            }

            stateDuration = 6 * 60 * 20; // change state every 6 minutes
            LOGGER.debug("new wind state {}", state);
        }

        wasRaining = isRaining;
        wasThundering = isThundering;
    }

    public static void tick(ClientLevel world) {
        tickState(world);

        velocityNoise.tick();
        directionTrendNoise.tick();
        directionNoise.tick();

        float strength = velocityNoise.getNoise();
        float direction = directionTrendNoise.getLerp() + directionNoise.getNoise();

        /*
         LOGGER.printf(Level.DEBUG, "state %s strength %.2f -> %.2f direction var %.2f° -> %.2f°, trend %.2f° -> %.2f°",
         state.toString(),
         strengthNoise.getNoise(),
         strengthNoise.getRightNoise(),
         directionNoise.getNoise() * 360.0 / TAU,
         directionNoise.getRightNoise() * 360.0 / TAU,
         directionTrendNoise.getLerp() * 360.0 / TAU,
         directionTrendNoise.getRightNoise() * 360.0 / TAU);
         */

        // calculate wind velocity (in blocks / tick)
        windX = strength * Mth.cos(direction);
        windZ = strength * Mth.sin(direction);
    }

    protected enum State {
        CALM(0.05f, 0.05f, 0.2f),
        WINDY(0.05f, 0.3f, 0.7f),
        STORMY(0.05f, 0.6f, 1.1f);

        public final TriangularDistribution velocityDistribution;

        State(@SuppressWarnings("SameParameterValue") float minSpeed, float likelySpeed, float maxSpeed) {
            this.velocityDistribution = new TriangularDistribution(minSpeed, maxSpeed, likelySpeed, rng);
        }
    }
}
