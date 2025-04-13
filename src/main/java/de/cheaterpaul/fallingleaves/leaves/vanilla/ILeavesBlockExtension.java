package de.cheaterpaul.fallingleaves.leaves.vanilla;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ILeavesBlockExtension {

    void fallingLeaves$spawnLeaves(Level level, BlockPos blockPos, RandomSource randomSource);

    void fallingLeaves$makeFallingLeavesParticles(Level level, BlockPos blockPos, RandomSource randomSource, BlockState belowState, BlockPos belowPos);
}
