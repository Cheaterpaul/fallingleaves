package de.cheaterpaul.fallingleaves.leaves.mod;

import de.cheaterpaul.fallingleaves.config.Config;
import de.cheaterpaul.fallingleaves.data.LeafLoader;
import de.cheaterpaul.fallingleaves.leaves.ILeavesSpawner;
import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafSetting;
import de.cheaterpaul.fallingleaves.leaves.mod.util.LeafHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import static net.minecraft.world.level.block.Block.isFaceFull;

public class ModLeavesSpawner implements ILeavesSpawner {

    private final ParticleEngine particleEngine;
    private final FallingLeafParticle.LeavesParticleFactory particleFactory;

    public ModLeavesSpawner() {
        this.particleEngine = Minecraft.getInstance().particleEngine;
        this.particleFactory = new FallingLeafParticle.LeavesParticleFactory();
    }

    @Override
    public void spawnFallingLeavesParticle(ClientLevel level, BlockPos pos, BlockState state, RandomSource randomSource, LeafSetting.LoadedLeafSetting leafSetting) {
        double x = pos.getX() + randomSource.nextDouble();
        double y = pos.getY() - (randomSource.nextDouble() / 3);
        double z = pos.getZ() + randomSource.nextDouble();

        double[] color = LeafHelper.getBlockTextureColor(state, level, pos);

        double r = color[0];
        double g = color[1];
        double b = color[2];

        var particle = this.particleFactory.createParticle(null, level, x, y, z, r, g, b, leafSetting);
        if (particle != null) {
            this.particleEngine.add(particle);
        }

    }

    @Override
    public void trySpawnFallingLeavesParticle(ClientLevel level, BlockPos pos, BlockState state, RandomSource randomSource) {
        LeafSetting.LoadedLeafSetting leafSetting = LeafLoader.getOrDefault(state.getBlock());
        if (leafChance(leafSetting, randomSource)) {
            spawnFallingLeavesParticle(level, pos, state, randomSource, leafSetting);
        }
    }

    @Override
    public void makeFallingLeavesParticles(ClientLevel level, BlockPos blockPos, RandomSource randomSource, BlockState belowState, BlockPos belowPos) {
        if (canSpawnParticle(level, blockPos, belowState, belowPos)) {
            this.trySpawnFallingLeavesParticle(level, blockPos,level.getBlockState(blockPos), randomSource);
        }
    }

    private boolean canSpawnParticle(ClientLevel level, BlockPos pos, BlockState belowState, BlockPos belowPos) {
        if (isFaceFull(belowState.getCollisionShape(level, belowPos), Direction.UP)) {
            return false;
        }

        double y2 = pos.getY() - Config.CONFIG.leaves.mod.minimumFreeSpaceBelow.get();
        AABB collisionBox = new AABB(pos.getX() + 0.1, pos.getY(), pos.getZ() + 0.1, pos.getX() + 0.9, y2, pos.getZ() + 0.9);

        // Only spawn the particle if there's enough room for it
        return !level.getBlockCollisions(null, collisionBox).iterator().hasNext();
    }

    private boolean leafChance(LeafSetting.LoadedLeafSetting leafSetting, RandomSource randomSource) {
        double spawnChance = leafSetting.leafType().type().spawnModifier() * leafSetting.setting().spawnRate();
        spawnChance *= Config.CONFIG.leaves.mod.leafSpawnRate.get() / 500f;

        return randomSource.nextDouble() < spawnChance;
    }

    @Override
    public boolean isVanilla() {
        return false;
    }
}
