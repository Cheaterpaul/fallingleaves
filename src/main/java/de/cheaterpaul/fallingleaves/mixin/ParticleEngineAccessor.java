package de.cheaterpaul.fallingleaves.mixin;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashSet;
import java.util.Set;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineAccessor {

    @Redirect(method = "reload(Lnet/minecraft/server/packs/resources/PreparableReloadListener$PreparationBarrier;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;Lnet/minecraft/util/profiling/ProfilerFiller;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;keySet()Ljava/util/Set;"))
    private Set<ResourceLocation> fallingleaves_particle_reg_name_3(Registry<ParticleType<?>> registry) {
        Set<ResourceLocation> set = new HashSet<>(registry.keySet());
//            set.add(new ResourceLocation("fallingleaves:falling_leaf"));
//            set.add(new ResourceLocation("fallingleaves:falling_leaf_conifer"));
        return set;
    }
}
