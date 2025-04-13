package de.cheaterpaul.fallingleaves.leaves.mod;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.config.Config;
import de.cheaterpaul.fallingleaves.data.LeafLoader;
import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafSetting;
import de.cheaterpaul.fallingleaves.leaves.mod.util.RenderSettings;
import de.cheaterpaul.fallingleaves.wind.IWindLevel;
import de.cheaterpaul.fallingleaves.wind.Wind;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FallingLeafParticle extends TextureSheetParticle {

    public static final ParticleRenderType LEAVES_SHEET = new ParticleRenderType("FALLINGLEAVES_PARTICLE_SHEET_TRANSLUCENT", RenderType.opaqueParticle(RenderSettings.LEAVES_ATLAS), true);

    protected static final float TAU = (float) (2 * Math.PI); // 1 rotation

    protected static final int FADE_DURATION = 16; // ticks
    // protected static final double FRICTION       = 0.30;
    protected static final double WATER_FRICTION = 0.05;

    protected final float windCoefficient; // to emulate drag/lift

    protected final float maxRotateSpeed; // rotations / tick
    protected final int maxRotateTime;
    protected int rotateTime = 0;

    protected FallingLeafParticle(ClientLevel clientWorld, double x, double y, double z, double r, double g, double b, @NotNull LeafSetting.LoadedLeafSetting provider) {
        super(clientWorld, x, y, z, 0, 0, 0);
        this.pickSprite(provider.leafType().spriteSet());

        this.gravity = 0.08f + random.nextFloat() * 0.04f;
        this.windCoefficient = 0.6f + random.nextFloat() * 0.4f;

        // the Particle constructor adds random noise to the velocity which we don't want
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;

        this.hasPhysics = true; // TODO: is it possible to turn off collisions with leaf blocks?
        this.lifetime = (int) (Config.CONFIG.leaves.mod.leafLifespan.get() * provider.leafType().type().lifeSpanModifier());

        this.rCol = (float) r;
        this.gCol = (float) g;
        this.bCol = (float) b;
        // accelerate over 3-7 seconds to at most 2.5 rotations per second
        this.maxRotateTime = (3 + random.nextInt(4 + 1)) * 20;
        this.maxRotateSpeed = (random.nextBoolean() ? -1 : 1) * (0.1f + 2.4f * random.nextFloat()) * TAU / 20f;

        this.roll = this.oRoll = random.nextFloat() * TAU;

        this.quadSize = (Config.CONFIG.leaves.mod.leafSize.get() / 50f) * provider.leafType().type().sizeModifier();
    }

    @Override
    public void tick() {
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.oRoll = this.roll;

        age++;

        // fade-out animation
        if (age >= lifetime + 1 - FADE_DURATION) {
            this.alpha -= 1F / FADE_DURATION;
        }

        if (age >= lifetime) {
            this.remove();
            return;
        }

        if (this.level.getFluidState(new BlockPos((int) x, (int) y, (int) z)).is(FluidTags.WATER)) {
            // float on water
            yd = 0.0;
            rotateTime = 0;

            xd *= (1 - WATER_FRICTION);
            zd *= (1 - WATER_FRICTION);
        } else {
            // apply gravity
            yd -= 0.04 * gravity;

            if (!onGround) {
                // spin when in the air
                rotateTime = Math.min(rotateTime + 1, maxRotateTime);
                this.roll += (rotateTime / (float) maxRotateTime) * maxRotateSpeed;
            } else {
                rotateTime = 0;

                // TODO: field_21507 inside move() makes particles stop permanently once they fall on the ground
                //       that is nice sometimes, but some/most leaves should still get blown along the ground by the wind
                // velocityX *= (1 - FRICTION);
                // velocityZ *= (1 - FRICTION);
            }

            // approach the target wind velocity over time via vel += (target - vel) * f, where f is in (0, 1)
            // after n ticks, the distance closes to a factor of 1 - (1 - f)^n.
            // for f = 1 / 2, it would only take 4 ticks to close the distance by 90%
            // for f = 1 / 60, it takes ~2 seconds to halve the distance, ~5 seconds to reach 80%
            //
            // the wind coefficient is just another factor in (0, 1) to add some variance between leaves.
            // this implementation lags behind the actual wind speed and will never reach it fully,
            // so wind speeds needs to be adjusted accordingly
            var wind = ((IWindLevel) this.level).fallingLeaves$getWind();
            xd += (wind.getWindX() - xd) * windCoefficient / 60.0f;
            zd += (wind.getWindZ() - zd) * windCoefficient / 60.0f;
        }

        move(xd, yd, zd);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return LEAVES_SHEET;
    }

    @OnlyIn(Dist.CLIENT)
    public static class LeavesParticleFactory implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(@Nullable SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double r, double g, double b) {
            return createParticle(parameters, world, x, y, z, r, g, b, null);
        }

        public Particle createParticle(@Nullable SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double r, double g, double b, @Nullable LeafSetting.LoadedLeafSetting settings) {
            if (settings == null) {
                settings = LeafLoader.getDefault();
            }
            return new FallingLeafParticle(world, x, y, z, r, g, b, settings);
        }

    }
}
