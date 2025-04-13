package de.cheaterpaul.fallingleaves.mixin.snow;

import de.cheaterpaul.fallingleaves.snow.ISnowLevel;
import de.cheaterpaul.fallingleaves.snow.Snow;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {


    @Shadow @Final protected float leafParticleChance;

    @Inject(method = "animateTick", at = @At("RETURN"))
    private void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource, CallbackInfo ci) {
        Snow snow = ((ISnowLevel) level).fallingLeaves$getSnow();
        BlockPos below = pos.below();
        BlockState blockstate = level.getBlockState(below);
        snow.makeSnowParticles(level, pos, randomSource, blockstate, below, this.leafParticleChance);
    }
}
