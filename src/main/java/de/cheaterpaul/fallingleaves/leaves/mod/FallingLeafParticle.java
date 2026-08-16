package de.cheaterpaul.fallingleaves.leaves.mod;

import com.mojang.blaze3d.vertex.*;
import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafType;
import de.cheaterpaul.fallingleaves.particle.ColoredSpriteProvider;
import de.cheaterpaul.fallingleaves.config.Config;
import de.cheaterpaul.fallingleaves.data.LeafLoader;
import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafSetting;
import de.cheaterpaul.fallingleaves.leaves.mod.util.RenderSettings;
import de.cheaterpaul.fallingleaves.wind.IWindLevel;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class FallingLeafParticle extends SingleQuadParticle {

    public static final ParticleRenderType LEAVES_SHEET = new ParticleRenderType("FALLINGLEAVES_PARTICLE_SHEET_TRANSLUCENT");
    public static final Layer LAYER = new Layer(true, RenderSettings.LEAVES_ATLAS , RenderPipelines.TRANSLUCENT_PARTICLE);

    protected static final float TAU = (float) (2 * Math.PI); // 1 rotation

    protected static final int FADE_DURATION = 16; // ticks
    // protected static final double FRICTION       = 0.30;
    protected static final double WATER_FRICTION = 0.05;

    protected final float windCoefficient; // to emulate drag/lift

    protected final float maxRotateSpeed; // rotations / tick
    protected final int maxRotateTime;
    protected int rotateTime = 0;

    protected FallingLeafParticle(ClientLevel clientWorld, double x, double y, double z, double r, double g, double b, @NotNull LeafSetting.LoadedLeafSetting provider, ColoredSpriteProvider.TextureSprite sprite) {
        super(clientWorld, x, y, z, 0, 0, 0, sprite.sprite());
        LeafType.LoadedLeafType loadedType = provider.leafType();
        LeafType type = loadedType.type();
        this.gravity = 0.08f + random.nextFloat() * 0.04f;
        this.windCoefficient = 0.3f + random.nextFloat() * 0.2f;

        // the Particle constructor adds random noise to the velocity which we don't want
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;

        this.hasPhysics = true; // TODO: is it possible to turn off collisions with leaf blocks?
        this.lifetime = (int) (Config.CONFIG.leaves.mod.leafLifespan.get() * type.lifeSpanModifier());

        this.rCol = (float) r;
        this.gCol = (float) g;
        this.bCol = (float) b;
        // accelerate over 3-7 seconds to at most 2.5 rotations per second
        this.maxRotateTime = (3 + random.nextInt(4 + 1)) * 20;
        this.maxRotateSpeed = (random.nextBoolean() ? -1 : 1) * (0.1f + 2.4f * random.nextFloat()) * TAU / 20f;

        this.roll = this.oRoll = random.nextFloat() * TAU;

        float mod = (2 + random.nextFloat())/ 2.5f;

        this.quadSize = (Config.CONFIG.leaves.mod.leafSize.get() / 50f) * type.sizeModifier() * mod;
        if (sprite.isTinted()) {
            this.rCol = 1;
            this.gCol = 1;
            this.bCol = 1;
        }
        this.quadSize *= sprite.sizeModifier();
    }

    @Override
    protected @NonNull Layer getLayer() {
        return LAYER;
    }

    @Override
    public void tick() {
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.oRoll = this.roll;

        this.age++;

        // fade-out animation
        if (this.age >= this.lifetime + 1 - FADE_DURATION) {
            this.alpha -= 1F / FADE_DURATION;
        }

        if (this.age >= this.lifetime) {
            this.remove();
            return;
        }

        if (this.level.getFluidState(new BlockPos((int) this.x, (int) this.y, (int) this.z)).is(FluidTags.WATER)) {
            // float on water
            this.yd = 0.0;
            this.rotateTime = 0;

            this.xd *= (1 - WATER_FRICTION);
            this.zd *= (1 - WATER_FRICTION);
        } else {
            // apply gravity
            this.yd -= 0.02 * this.gravity;

            if (!onGround) {
                // spin when in the air
                this.rotateTime = Math.min(this.rotateTime + 1, this.maxRotateTime);
                this.roll += (this.rotateTime / (float) this.maxRotateTime) * this.maxRotateSpeed;

                // approach the target wind velocity over time via vel += (target - vel) * f, where f is in (0, 1)
                // after n ticks, the distance closes to a factor of 1 - (1 - f)^n.
                // for f = 1 / 2, it would only take 4 ticks to close the distance by 90%
                // for f = 1 / 60, it takes ~2 seconds to halve the distance, ~5 seconds to reach 80%
                //
                // the wind coefficient is just another factor in (0, 1) to add some variance between leaves.
                // this implementation lags behind the actual wind speed and will never reach it fully,
                // so wind speeds needs to be adjusted accordingly
                var wind = ((IWindLevel) this.level).fallingLeaves$getWind();
                // Apply wind effect similar to vanilla implementation
                // Don't multiply wind by coefficient before adjustment
                // Use coefficient divided by 60f for slower, more natural adjustment
                this.xd += (wind.getWindX() - this.xd) * this.windCoefficient / 60f;
                this.zd += (wind.getWindZ() - this.zd) * this.windCoefficient / 60f;
            } else {
                this.rotateTime = 0;

                this.xd = 0;
                this.zd = 0;
            }
        }

        float fadingDuration = Math.min(40, this.lifetime / 4f);
        int remainingLifespan = this.lifetime - this.age;

        if (remainingLifespan < fadingDuration) {
            this.alpha = remainingLifespan / fadingDuration;
        }

        if (!this.onGround || checkGroundCollision()) {
            move(this.xd, this.yd, this.zd);
        }
    }

    private boolean checkGroundCollision() {
        BlockPos blockPos = new BlockPos((int) this.x, (int) this.y, (int) this.z);
        var collision = List.of(level.getBlockState(blockPos).getCollisionShape(level, blockPos),level.getBlockState(blockPos.below()).getCollisionShape(level, blockPos.below()));
        return Shapes.collide(Direction.Axis.Y, getBoundingBox().move(0,0.1,0), collision, 0.1) > 0.1;
    }

    @Override
    public @NotNull ParticleRenderType getGroup() {
        return LEAVES_SHEET;
    }

    private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);

    @Override
    public void move(double pX, double pY, double pZ) {
        double d0 = pX;
        double d1 = pY;
        double d2 = pZ;

        if (this.hasPhysics && (pX != 0.0 || pY != 0.0 || pZ != 0.0) && pX * pX + pY * pY + pZ * pZ < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
            Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(pX, pY, pZ), this.getBoundingBox(), this.level, List.of());
            pX = vec3.x;
            pY = vec3.y;
            pZ = vec3.z;
            List<Entity> entities = this.level.getEntities(null, getBoundingBox());
            if (!entities.isEmpty() && random.nextFloat() > 0.4f) {
                Entity first = entities.getFirst();
                pX += first.getDeltaMovement().x;
                pZ += first.getDeltaMovement().z;
            }
        }

        if (pX != 0.0 || pY != 0.0 || pZ != 0.0) {
            this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
            this.setLocationFromBoundingbox();
        }

        this.onGround = d1 != pY && d1 < 0.0;
        if (d0 != pX) {
            this.xd = 0.0;
        }

        if (d2 != pZ) {
            this.zd = 0.0;
        }

        if (this.onGround && Config.CONFIG.leaves.mod.particlesDisappearOnGroundContact.get()) {
            this.remove();
        }
    }

    @Override
    public void extract(@NonNull QuadParticleRenderState particleTypeRenderState, @NonNull Camera camera, float partialTickTime) {
        Quaternionf q = new Quaternionf();
        if (this.onGround) {
            q.rotateX((float) Math.PI / -2f);
            q.rotateZ(hashCode());


            Vec3 pos = getPos();
            Vec3 position = camera.position();
            Vec3 subtract = position.subtract(pos);
            if (subtract.y < 0) {
                q.rotateY((float) Math.PI);
                q.rotateZ((float) Math.PI/2);
            }
        } else {
            getFacingCameraMode().setRotation(q, camera, partialTickTime);
            if (this.roll != 0.0) {
                q.rotateZ(Mth.lerp(partialTickTime, this.oRoll, this.roll));
            }
        }
        super.extract(particleTypeRenderState, camera, partialTickTime);
    }

    @Override
    protected void extractRotatedQuad(@NonNull QuadParticleRenderState particleTypeRenderState, @NonNull Quaternionf rotation, float x, float y, float z, float partialTickTime) {
        super.extractRotatedQuad(particleTypeRenderState,rotation,x, y + switch (hashCode() % 3) {
            case 0 -> 0.01f;
            case 1 -> 0.02f;
            default -> 0.03f;
        }, z, partialTickTime);
    }

    public static class LeavesParticleFactory implements ParticleProvider<SimpleParticleType> {

        public Particle createParticle(ClientLevel level, double x, double y, double z, double r, double g, double b, RandomSource random, @Nullable LeafSetting.LoadedLeafSetting settings) {
            if (settings == null) {
                settings = LeafLoader.getDefault();
            }
            ColoredSpriteProvider.TextureSprite textureSprite = settings.leafType().spriteSet().get(random);
            return new FallingLeafParticle(level, x, y, z, r, g, b, settings, textureSprite);
        }

        @Override
        public @Nullable Particle createParticle(@Nullable SimpleParticleType options, @NonNull ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, @NonNull RandomSource random) {
            return createParticle(level, x, y, z, 0 ,0 ,0, random, null);
        }

    }
}
