package de.cheaterpaul.fallingleaves.snow;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.config.Config;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.Block.isFaceFull;

public class Snow {

    public void makeSnowParticles(Level level, BlockPos pos, RandomSource randomSource, BlockState state, BlockPos posBelow, float leafParticleChance) {
        if (Config.CONFIG.snow.enabled.get() && !(randomSource.nextFloat() >= leafParticleChance) && !isFaceFull(state.getCollisionShape(level, posBelow), Direction.UP)) {
            this.spawnSnowParticles(level, pos, randomSource);
        }
    }

    public void makeDecayingSnowParticles(Level level, BlockPos pos, RandomSource randomSource, BlockState state, BlockPos posBelow) {
        if (Config.CONFIG.snow.enabled.get() && !isFaceFull(state.getCollisionShape(level, posBelow), Direction.UP)) {
            for (int i = 0; i < 5; i++) {
                this.spawnSnowParticles(level, pos, randomSource);
            }
        }
    }

    public void makeAttackSnowParticles(ClientLevel level, BlockPos pos, BlockState blockState, RandomSource randomSource, BlockPos posBelow, BlockState stateBelow) {

    }

    public void spawnSnowParticles(Level level, BlockPos pos, RandomSource randomSource) {
        if (canSpawnSnow(level, pos)) {
            ParticleUtils.spawnParticleBelow(level, pos, randomSource, ParticleTypes.SNOWFLAKE);
        }
    }

    private boolean canSpawnSnow(Level level, BlockPos pos) {
        for (int i = 1; i <= 5; ++i) {
            var state = level.getBlockState(pos.above(i));
            if (state.is(BlockTags.SNOW)) {
                return true;
            }

            if (!state.is(BlockTags.LEAVES)) {
                return false;
            }
        }
        return false;
    }
}
