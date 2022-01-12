package de.cheaterpaul.fallingleaves.mixin;

import de.cheaterpaul.fallingleaves.init.IParticleEngine;
import de.cheaterpaul.fallingleaves.init.Leaves;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineAccessor implements IParticleEngine {

    @Shadow @Final private Map<ResourceLocation, ParticleEngine.MutableSpriteSet> spriteSets;

    @Shadow @Final private Map<ResourceLocation, ParticleProvider<?>> providers;

    @Override
    public <T extends ParticleOptions> void registerModded(ParticleType<T> p_107379_, ParticleEngine.SpriteParticleRegistration<T> p_107380_) {
        ParticleEngine.MutableSpriteSet particleengine$mutablespriteset = new ParticleEngine.MutableSpriteSet();
        this.spriteSets.put(p_107379_.getRegistryName(), particleengine$mutablespriteset);
        this.providers.put(p_107379_.getRegistryName(), p_107380_.create(particleengine$mutablespriteset));
    }

    @Redirect(method = "reload(Lnet/minecraft/server/packs/resources/PreparableReloadListener$PreparationBarrier;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;Lnet/minecraft/util/profiling/ProfilerFiller;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;keySet()Ljava/util/Set;"))
    private Set<ResourceLocation> fallingleaves_particle_reg_name_3(Registry<ParticleType<?>> registry) {
        Set<ResourceLocation> set = new HashSet<>(registry.keySet());
        if (Leaves.ADDED) {
            set.add(Leaves.falling_leaf.getRegistryName());
            set.add(Leaves.falling_leaf_conifer.getRegistryName());
        }
        return set;
    }

    @Redirect(method = "makeParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;getKey(Ljava/lang/Object;)Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation fallingleaves_particle_reg_name_4(Registry<ParticleType<?>> registry, Object particle) {
        return getLoc(registry, particle);
    }

    private ResourceLocation getLoc(Registry<ParticleType<?>> registry, Object p_123006_) {
        if ( Leaves.ADDED && (p_123006_ == Leaves.falling_leaf || p_123006_ == Leaves.falling_leaf_conifer)) {
            return ((ParticleType<?>) p_123006_).getRegistryName();
        } else {
            return registry.getKey((ParticleType<?>) p_123006_);
        }
    }
}
