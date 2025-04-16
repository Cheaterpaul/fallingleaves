package de.cheaterpaul.fallingleaves.leaves;

import de.cheaterpaul.fallingleaves.data.LeafLoader;
import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafSetting;
import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafTypes;
import de.cheaterpaul.fallingleaves.seasons.ISeasonProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public interface ILeavesSpawner {

    void trySpawnFallingLeavesParticle(ClientLevel level, BlockPos pos, BlockState state, RandomSource randomSource);

    default void spawnFallingLeavesParticle(ClientLevel level, BlockPos pos, BlockState state, RandomSource randomSource) {
        spawnFallingLeavesParticle(level, pos, state, randomSource, LeafLoader.get(state.getBlock()));
    }

    void spawnFallingLeavesParticle(ClientLevel level, BlockPos pos, BlockState state, RandomSource randomSource, LeafSetting.LoadedLeafSetting leafSetting);

    void makeFallingLeavesParticles(ClientLevel level, BlockPos blockPos, RandomSource randomSource, BlockState belowState, BlockPos belowPos);

    boolean isVanilla();

    void updateSeasonProvider(ISeasonProvider seasonProvider);
}
