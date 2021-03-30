package de.cheaterpaul.fallingleaves.particle;

import de.cheaterpaul.fallingleaves.init.Config;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * TODO - Plenty of "Magic numbers" in this class that we may want to get rid of
 *        or, at the very least, define as class constants at the head of the file.
 */

@OnlyIn(Dist.CLIENT)
public class FallingLeafParticle extends SpriteTexturedParticle {

    private final float rotateFactor;

    protected FallingLeafParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, IAnimatedSprite provider) {
        super(clientWorld, x, y, z, r, g, b); // Note: will set velocity to (r, g, b)
        this.setSpriteFromAge(provider);
        this.hasPhysics = true;
        this.gravity = 0.1F;
        this.lifetime = Config.CONFIG.leafLifespan.get();

        this.xd *= 0.3F;
        this.yd *= 0.0F;
        this.zd *= 0.3F;

        this.rCol   = (float) r;
        this.gCol = (float) g;
        this.bCol  = (float) b;
        this.rotateFactor = ((float) Math.random() - 0.5F) * 0.01F;

        this.quadSize = Config.CONFIG.leafSize.get() / 50f;
    }

    public void tick() {
        super.tick();

        if (this.age < 2) {
            this.yd = 0;
        }

        if (this.age > this.lifetime - 1 / 0.06F) {
            if (this.alpha > 0.06F) {
                this.alpha -= 0.06F;
            } else {
                this.remove();
            }
        }

        this.oRoll = this.roll;

        if (!this.onGround && !this.level.getFluidState(new BlockPos(this.x, this.y, this.z)).is(FluidTags.WATER)) {
            this.roll += Math.PI * Math.sin(this.rotateFactor * this.age) / 2;
        }

        if (this.level.getFluidState(new BlockPos(this.x, this.y, this.z)).is(FluidTags.WATER)) {
            this.yo = 0;
            this.gravity = 0;
        } else {
            this.gravity = 0.1F;
        }
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class DefaultFactory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite provider;

        public DefaultFactory(IAnimatedSprite provider) {
            this.provider = provider;
        }

        @Override
        public Particle createParticle(BasicParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new FallingLeafParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.provider);
        }
    }
}
