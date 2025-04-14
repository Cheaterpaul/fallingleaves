package de.cheaterpaul.fallingleaves.leaves.vanilla;

import de.cheaterpaul.fallingleaves.leaves.ILeavesSpawner;
import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafSetting;
import de.cheaterpaul.fallingleaves.seasons.ISeasonProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class VanillaLeavesSpawner implements ILeavesSpawner {

    private final ISeasonProvider seasonProvider;

    public VanillaLeavesSpawner(ISeasonProvider seasonProvider) {
        this.seasonProvider = seasonProvider;
    }

    @Override
    public void trySpawnFallingLeavesParticle(ClientLevel level, BlockPos pos, BlockState state, RandomSource randomSource) {
        if (state.getBlock() instanceof ILeavesBlockExtension leavesBlock) {
            leavesBlock.fallingLeaves$spawnLeaves(level, pos, randomSource);
        }
    }

    @Override
    public void spawnFallingLeavesParticle(ClientLevel level, BlockPos pos, BlockState state, RandomSource randomSource, LeafSetting.LoadedLeafSetting leafSetting) {
        if (state.getBlock() instanceof ILeavesBlockExtension leavesBlock) {
            leavesBlock.fallingLeaves$spawnLeaves(level, pos, randomSource);
        }
    }

    @Override
    public void makeFallingLeavesParticles(ClientLevel level, BlockPos blockPos, RandomSource randomSource, BlockState belowState, BlockPos belowPos) {
        if (level.getBlockState(blockPos).getBlock() instanceof ILeavesBlockExtension leavesBlock) {
            leavesBlock.fallingLeaves$makeFallingLeavesParticles(level, blockPos, randomSource, belowState, belowPos);
        }
    }

    @Override
    public boolean isVanilla() {
        return true;
    }
}
