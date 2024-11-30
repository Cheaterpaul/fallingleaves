package de.cheaterpaul.fallingleaves.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.cheaterpaul.fallingleaves.util.FallingLeafParticle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"))
    private static <E> ImmutableList<E> onClinit(E e1, E e2, E e3, E e4, E e5, Operation<ImmutableList<E>> original) {
        var list = new ArrayList<>(original.call(e1, e2, e3, e4, e5));
        int i = list.indexOf(ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT);
        list.add(i + 1, (E) FallingLeafParticle.LEAVES_SHEET);
        return ImmutableList.copyOf(list);
    }
}
