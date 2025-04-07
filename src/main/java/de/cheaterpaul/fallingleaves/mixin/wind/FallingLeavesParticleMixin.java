package de.cheaterpaul.fallingleaves.mixin.wind;

import de.cheaterpaul.fallingleaves.wind.IWindLevel;
import de.cheaterpaul.fallingleaves.wind.Wind;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FallingLeavesParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingLeavesParticle.class)
public abstract class FallingLeavesParticleMixin extends TextureSheetParticle {

    @Deprecated
    private FallingLeavesParticleMixin(ClientLevel p_108323_, double p_108324_, double p_108325_, double p_108326_) {
        super(p_108323_, p_108324_, p_108325_, p_108326_);
    }

    @Unique
    private float fallingLeaves$windCoefficient;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(ClientLevel level, double x, double y, double z, SpriteSet sprites, float gravityMultiplier, float windBig, boolean swirl, boolean flowAway, float size, float ySpeed, CallbackInfo ci) {
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
