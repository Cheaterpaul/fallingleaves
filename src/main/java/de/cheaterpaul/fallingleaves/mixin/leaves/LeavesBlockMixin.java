package de.cheaterpaul.fallingleaves.mixin.leaves;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.cheaterpaul.fallingleaves.leaves.ILeavesLevel;
import de.cheaterpaul.fallingleaves.leaves.vanilla.ILeavesBlockExtension;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin implements ILeavesBlockExtension {


    @Shadow protected abstract void spawnFallingLeavesParticle(Level level, BlockPos blockPos, RandomSource randomSource);

    @Override
    public void fallingLeaves$spawnLeaves(Level level, BlockPos blockPos, RandomSource randomSource) {
        this.spawnFallingLeavesParticle(level, blockPos, randomSource);
    }

    @Shadow
    protected abstract void makeFallingLeavesParticles(Level level, BlockPos pos, RandomSource randomSource, BlockState belowState, BlockPos belowPos);

    @Override
    public void fallingLeaves$makeFallingLeavesParticles(Level level, BlockPos blockPos, RandomSource randomSource, BlockState belowState, BlockPos belowPos) {
        this.makeFallingLeavesParticles(level, blockPos, randomSource, belowState, belowPos);
    }

    @WrapOperation(method = "animateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LeavesBlock;makeFallingLeavesParticles(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V"))
    private void replaceFallingLeaves(LeavesBlock block, Level level, BlockPos pos, RandomSource randomSource, BlockState belowState, BlockPos belowPos, Operation<Void> original) {
        if (level instanceof ClientLevel clientLevel) {
            ((ILeavesLevel) clientLevel).fallingLeaves$getLeaves().makeLeavesParticles(clientLevel, pos, randomSource, belowState, belowPos);
        } else {
            original.call(block, level, pos, randomSource, belowState, belowPos);
        }
    }
}
