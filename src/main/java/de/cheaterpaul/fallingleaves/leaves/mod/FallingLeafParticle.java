package de.cheaterpaul.fallingleaves.leaves.mod;

import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;

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

        this.pickSprite(loadedType.spriteSet());
    }

    public void pickSprite(ColoredSpriteProvider sprite) {
        ColoredSpriteProvider.TextureSprite textureSprite = sprite.get(this.random);
        this.setSprite(textureSprite.sprite());
        if (textureSprite.isTinted()) {
            this.rCol = 1;
            this.gCol = 1;
            this.bCol = 1;
        }
        this.quadSize *= textureSprite.sizeModifier();
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

        double windX = 0;
        double windZ = 0;

        if (this.level.getFluidState(new BlockPos((int) this.x, (int) this.y, (int) this.z)).is(FluidTags.WATER)) {
            // float on water
            this.yd = 0.0;
            this.rotateTime = 0;

            this.xd *= (1 - WATER_FRICTION);
            this.zd *= (1 - WATER_FRICTION);
        } else {
            // apply gravity
            this.yd -= 0.04 * this.gravity;

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
                windX = wind.getWindX() * this.windCoefficient;
                windZ = wind.getWindZ() * this.windCoefficient;
                //this.xd += (windX - this.xd) * 0.1;
               // this.zd += (windZ - this.zd) * 0.1;
            } else {
                this.rotateTime = 0;

                this.xd = 0;
                this.zd = 0;
            }
        }

        move(this.xd + windX, this.yd, this.zd + windZ);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
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
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        Quaternionf q = new Quaternionf();
        if (this.onGround) {
            q.rotateX((float) Math.PI / -2f);
            q.rotateZ(hashCode());


            Vec3 pos = getPos();
            Vec3 position = pRenderInfo.getPosition();
            Vec3 subtract = position.subtract(pos);
            if (subtract.y < 0) {
                q.rotateY((float) Math.PI);
                q.rotateZ((float) Math.PI/2);
            }
        } else {
            getFacingCameraMode().setRotation(q, pRenderInfo, pPartialTicks);
            if (this.roll != 0.0) {
                q.rotateZ(Mth.lerp(pPartialTicks, this.oRoll, this.roll));
            }
        }
        super.renderRotatedQuad(pBuffer, pRenderInfo, q, pPartialTicks);
    }

    @Override
    protected void renderRotatedQuad(@NotNull VertexConsumer pBuffer, @NotNull Quaternionf pQuaternion, float pX, float pY, float pZ, float pPartialTicks) {
        super.renderRotatedQuad(pBuffer, pQuaternion, pX, pY + switch (hashCode() % 3) {
            case 0 -> 0.01f;
            case 1 -> 0.02f;
            default -> 0.03f;
        }, pZ, pPartialTicks);
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
