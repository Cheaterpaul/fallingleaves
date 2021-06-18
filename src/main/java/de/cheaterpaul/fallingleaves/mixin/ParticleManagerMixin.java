package de.cheaterpaul.fallingleaves.mixin;

import de.cheaterpaul.fallingleaves.util.Wind;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @Shadow
    protected ClientWorld level;

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        Wind.tick(level);
    }

}
