/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 RandomMcSomethin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.cheaterpaul.fallingleaves.particle;

import de.cheaterpaul.fallingleaves.init.FallingLeavesConfig;
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
        this.lifetime = FallingLeavesConfig.CONFIG.leafLifespan.get();

        this.xd *= 0.3F;
        this.yd *= 0.0F;
        this.zd *= 0.3F;

        this.rCol   = (float) r;
        this.gCol = (float) g;
        this.bCol  = (float) b;
        this.rotateFactor = ((float) Math.random() - 0.5F) * 0.01F;

        this.quadSize = FallingLeavesConfig.CONFIG.leafSize.get() / 50f;
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
