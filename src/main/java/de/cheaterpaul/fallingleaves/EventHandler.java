/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 RandomMcSomethin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.cheaterpaul.fallingleaves;

import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import de.cheaterpaul.fallingleaves.data.LeafLoader;
import de.cheaterpaul.fallingleaves.modcompat.SereneSeasons;
import de.cheaterpaul.fallingleaves.util.LeafUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = FallingLeavesMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class EventHandler {

    /**
     * Spawn between 0 and 3 leaves on hitting a leaf block
     */
    @SubscribeEvent
    public static void onAttackLeavesBlock(PlayerInteractEvent.LeftClickBlock e) {
        if (e.getLevel().isClientSide) {
            BlockState state = e.getLevel().getBlockState(e.getPos());
            ResourceLocation location = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            LeafSettingsEntry leafSettings = LeafLoader.getLeafSetting(location);
            if (leafSettings != null || state.getBlock() instanceof LeavesBlock) {
                // binomial distribution - extremes (0 or 3 leaves) are less likely
                for (int i = 0; i < 3; i++) {
                    if (e.getEntity().getRandom().nextBoolean()) {
                        LeafUtil.trySpawnLeafParticle(state, (ClientLevel) e.getLevel(), e.getPos(), e.getEntity().getRandom(), leafSettings);
                    }
                }
            }
        }
    }

    public static void spawnParticles(BlockState state, ClientLevel level, BlockPos pos, RandomSource random) {
        ResourceLocation location = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        LeafSettingsEntry leafSettings = LeafLoader.getLeafSetting(location);

        // Every leaf block has a settings entry, but some blocks are considered leaves when they technically aren't
        // E.g. terrestria:sakura_log can be "leaf-logged" - in that case, we simply ignore them
        if (leafSettings == null && !(state.getBlock() instanceof LeavesBlock)) {
            return;
        }

        if (!FallingLeavesMod.CONFIG.dropFromPlayerPlacedBlocks.get() && state.getValue(LeavesBlock.PERSISTENT)) {
            return;
        }

        spawnLeaves(state, level, pos, random, leafSettings);
        spawnSnow(state, level, pos, random, leafSettings);
    }

    public static void spawnLeaves(BlockState state, ClientLevel level, BlockPos pos, RandomSource random, LeafSettingsEntry leafSettings) {
        double spawnChance = 1;
        double modifier = FallingLeavesMod.CONFIG.leafSpawnRate.get();
        if (leafSettings != null) {
            spawnChance = leafSettings.spawnRateFactor();
            if (leafSettings.considerAsConifer()) {
                modifier = FallingLeavesMod.CONFIG.coniferLeafSpawnRate.get();
            }
        }
        modifier = modifier / 10f / 75f;
        spawnChance *= modifier;
        spawnChance *= SereneSeasons.getModifier(level);

        while (spawnChance > 0) {
            if (random.nextDouble() < spawnChance) {
                LeafUtil.trySpawnLeafParticle(state, level, pos, random, leafSettings);
            }
            spawnChance -= 1;
        }
    }

    public static void spawnSnow(BlockState state, ClientLevel level, BlockPos pos, RandomSource random, LeafSettingsEntry leafSettings) {
        if (canSnow(level, pos)) {
            double spawnChance = 1;
            double modifier = FallingLeavesMod.CONFIG.snowSpawnRate.get();
            modifier = modifier / 10f / 75;
            spawnChance *= modifier;
            if (level.isRaining() && level.getBiome(pos).value().getPrecipitationAt(pos) == Biome.Precipitation.SNOW) {
                spawnChance *= 5;
            }

            while (spawnChance > 0) {
                if (random.nextDouble() < spawnChance) {
                    LeafUtil.trySpawnSnowParticle(state, level, pos, random, leafSettings);
                }
                spawnChance -= 1;
            }
        }

    }

    private static boolean canSnow(ClientLevel level, BlockPos leavesPos) {
        if (FallingLeavesMod.CONFIG.disableSnow.get()) return false;
        BlockPos.MutableBlockPos pos = leavesPos.mutable();
        int distance = 0;
        while (distance < 5) {
            pos.move(Direction.UP);
            BlockState blockState = level.getBlockState(pos);
            if (blockState.is(BlockTags.SNOW)) {
                return true;
            }
            if (!LeavesBlock.isShapeFullBlock(blockState.getShape(level, pos))) {
                return false;
            }
            distance++;
        }
        return false;
    }

    public static void spawnDecayingParticles(BlockState state, ClientLevel level, BlockPos pos, RandomSource random) {
        ResourceLocation location = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        LeafSettingsEntry leafSettings = LeafLoader.getLeafSetting(location);

        // Every leaf block has a settings entry, but some blocks are considered leaves when they technically aren't
        // E.g. terrestria:sakura_log can be "leaf-logged" - in that case, we simply ignore them
        if (leafSettings == null && !(state.getBlock() instanceof LeavesBlock)) {
            return;
        }

        if (!FallingLeavesMod.CONFIG.dropFromPlayerPlacedBlocks.get() && state.getValue(LeavesBlock.PERSISTENT)) {
            return;
        }

        for (int i = 0; i < FallingLeavesMod.CONFIG.maxDecayingLeaves.get(); i++) {
            if (random.nextBoolean()) {
                LeafUtil.trySpawnLeafParticle(state, level, pos, random, leafSettings);
            }
        }

        for (int i = 0; i < FallingLeavesMod.CONFIG.maxDecayingSnowParticles.get(); i++) {
            if (random.nextBoolean()) {
                LeafUtil.trySpawnSnowParticle(state, level, pos, random, leafSettings);
            }
        }

    }

}
