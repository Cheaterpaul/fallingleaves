package de.cheaterpaul.fallingleaves.mixin;

import de.cheaterpaul.fallingleaves.init.Leaves;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashSet;
import java.util.Set;

@Mixin(ParticleManager.class)
public abstract class ParticleEngineAccessor {

    @Redirect(method = "register(Lnet/minecraft/particles/ParticleType;Lnet/minecraft/client/particle/ParticleManager$IParticleMetaFactory;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/Registry;getKey(Ljava/lang/Object;)Lnet/minecraft/util/ResourceLocation;"))
    private ResourceLocation fallingleaves_particle_reg_name_1(Registry<ParticleType<?>> registry, Object p_123006_) {
        return getLoc(registry, p_123006_);
    }

    @Redirect(method = "makeParticle(Lnet/minecraft/particles/IParticleData;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/Registry;getKey(Ljava/lang/Object;)Lnet/minecraft/util/ResourceLocation;"))
    private ResourceLocation fallingleaves_particle_reg_name_2(Registry<ParticleType<?>> registry, Object p_123006_) {
        return getLoc(registry, p_123006_);
    }

    @Redirect(method = "reload(Lnet/minecraft/resources/IFutureReloadListener$IStage;Lnet/minecraft/resources/IResourceManager;Lnet/minecraft/profiler/IProfiler;Lnet/minecraft/profiler/IProfiler;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/Registry;keySet()Ljava/util/Set;"))
    private Set<ResourceLocation> fallingleaves_particle_reg_name_3(Registry<ParticleType<?>> registry) {
        Set<ResourceLocation> set = new HashSet<>(registry.keySet());
        if (Leaves.ADDED) {
            set.add(Leaves.falling_leaf.getRegistryName());
            set.add(Leaves.falling_leaf_conifer.getRegistryName());
        }
        return set;
    }

    private ResourceLocation getLoc(Registry<ParticleType<?>> registry, Object p_123006_) {
        if (p_123006_ == Leaves.falling_leaf || p_123006_ == Leaves.falling_leaf_conifer) {
            return ((ParticleType<?>) p_123006_).getRegistryName();
        } else {
            return registry.getKey((ParticleType<?>) p_123006_);
        }
    }
}
