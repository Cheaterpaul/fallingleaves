package de.cheaterpaul.fallingleaves.mixin.decay;

import de.cheaterpaul.fallingleaves.leaves.ILeavesLevel;
import de.cheaterpaul.fallingleaves.snow.ISnowLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level {

    @Deprecated
    private ClientLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(method = "addDestroyBlockEffect", at = @At("HEAD"))
    public void addDecayParticle(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (state.is(BlockTags.LEAVES)) {
            BlockPos below = pos.below();
            BlockState blockstate = this.getBlockState(below);
            ((ISnowLevel) this).fallingLeaves$getSnow().makeDecayingSnowParticles(this, pos, random, blockstate, below);
            ((ILeavesLevel) this).fallingLeaves$getLeaves().makeDecayingLeavesParticles((ClientLevel) (Object) this, pos, state, random, blockstate, below);
        }
    }
}
