package de.cheaterpaul.fallingleaves.mixin.wind;

import de.cheaterpaul.fallingleaves.wind.IWindLevel;
import de.cheaterpaul.fallingleaves.wind.Wind;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FallingLeavesParticle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingLeavesParticle.class)
public abstract class FallingLeavesParticleMixin extends SingleQuadParticle {


    @Unique
    private float fallingLeaves$windCoefficient;

    @Deprecated
    private FallingLeavesParticleMixin(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
        super(level, x, y, z, sprite);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite, float fallAcceleration, float sideAcceleration, boolean swirl, boolean flowAway, float scale, float startVelocity, CallbackInfo ci) {
        this.fallingLeaves$windCoefficient = 0.6f + level.getRandom().nextFloat() * 0.4f;
    }

    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void applyWind(CallbackInfo ci) {
        if (!this.removed) {
            Wind wind = ((IWindLevel) this.level).fallingLeaves$getWind();
            if (wind.isActive()) {
                this.xd += (wind.getWindX() - this.xd) * this.fallingLeaves$windCoefficient / 60f;
                this.zd += (wind.getWindZ() - this.zd) * this.fallingLeaves$windCoefficient / 60f;
            }
        }
    }
}
