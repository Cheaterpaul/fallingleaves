package de.cheaterpaul.fallingleaves.leaves;

import de.cheaterpaul.fallingleaves.config.Config;
import de.cheaterpaul.fallingleaves.leaves.mod.ModLeavesSpawner;
import de.cheaterpaul.fallingleaves.leaves.vanilla.VanillaLeavesSpawner;
import de.cheaterpaul.fallingleaves.seasons.ISeasonProvider;
import de.cheaterpaul.fallingleaves.seasons.serene.SereneSeasonProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

import static net.minecraft.world.level.block.Block.isFaceFull;

public class Leaves {

    private final ClientLevel level;
    private ILeavesSpawner spawner;

    public Leaves(ClientLevel level) {
        this.level = level;
        checkSpawner(!Config.CONFIG.leaves.enabled.get());
    }

    public void checkSpawner(boolean useVanilla) {
        ISeasonProvider seasonProvider = createSeasonProvider();
        if (this.spawner == null) {
            this.spawner = useVanilla ? new VanillaLeavesSpawner(seasonProvider) : new ModLeavesSpawner(seasonProvider);
        } else if (this.spawner.isVanilla() != useVanilla) {
            if (useVanilla) {
                this.spawner = new VanillaLeavesSpawner(seasonProvider);
            } else {
                this.spawner = new ModLeavesSpawner(seasonProvider);
            }
        }
    }

    public ISeasonProvider createSeasonProvider() {
        return ModList.get().isLoaded("sereneseasons") && Config.CONFIG.leaves.seasons.enabled.get() ? new SereneSeasonProvider(this.level) : ISeasonProvider.DEFAULT;
    }

    public void makeDecayingLeavesParticles(ClientLevel level, BlockPos pos, BlockState state, RandomSource randomSource, BlockState stateBelow, BlockPos posBelow) {
        if (Config.CONFIG.snow.enabled.get() && !isFaceFull(stateBelow.getCollisionShape(level, posBelow), Direction.UP)) {
            for (int i = 0; i < 10; i++) {
                this.spawner.spawnFallingLeavesParticle(level, pos, state, randomSource);
            }
        }
    }

    public void makeLeavesParticles(ClientLevel level, BlockPos pos, RandomSource randomSource, BlockState state, BlockPos posBelow) {
        this.spawner.makeFallingLeavesParticles(level, pos, randomSource, state, posBelow);
    }

    public void makeAttackLeavesParticles(ClientLevel level, BlockPos pos, BlockState blockState, RandomSource random, BlockPos posBelow, BlockState stateBelow) {
        if (Config.CONFIG.snow.enabled.get() && !isFaceFull(stateBelow.getCollisionShape(level, posBelow), Direction.UP)) {
            for (int i = 0; i < 4; i++) {
                this.spawner.spawnFallingLeavesParticle(level, pos, blockState, random);
            }
        }
    }


}
