package de.cheaterpaul.fallingleaves.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.cheaterpaul.fallingleaves.util.FallingLeafParticle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @SuppressWarnings("unused")
    @Mutable
    @Shadow
    @Final
    private static List<ParticleRenderType> RENDER_ORDER;

    @WrapOperation(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/ParticleEngine;RENDER_ORDER:Ljava/util/List;"))
    private static void onClinit(List<ParticleRenderType> value, Operation<Void> original) {
        var newlist = new ArrayList<>(value);
        int i = value.indexOf(ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT);
        newlist.add(i + 1, FallingLeafParticle.LEAVES_SHEET);
        original.call(newlist);
    }
}
