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

package de.cheaterpaul.fallingleaves.mixin;

import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import de.cheaterpaul.fallingleaves.init.ClientMod;
import de.cheaterpaul.fallingleaves.init.FallingLeavesConfig;
//import de.cheaterpaul.fallingleaves.modcompat.SereneSeasons;
import de.cheaterpaul.fallingleaves.modcompat.SereneSeasons;
import de.cheaterpaul.fallingleaves.util.LeafUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(LeavesBlock.class)
public abstract class LeafTickMixin {

    @Inject(at = @At("HEAD"), method = "animateTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V")
    private void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        LeafSettingsEntry leafSettings = ClientMod.getLeafSetting(BuiltInRegistries.BLOCK.getKey(state.getBlock()));

        // Every leaf block has a settings entry, but some blocks are considered leaves when they technically aren't
        // E.g. terrestria:sakura_log can be "leaf-logged" - in that case, we simply ignore them
        if (leafSettings == null && !(state.getBlock() instanceof LeavesBlock))
            return;

        if (!FallingLeavesConfig.CONFIG.dropFromPlayerPlacedBlocks.get() && state.getValue(LeavesBlock.PERSISTENT))
            return;

        double spawnChance = 1;
        double modifier = FallingLeavesConfig.CONFIG.leafSpawnRate.get();
        if (leafSettings != null) {
            spawnChance = leafSettings.spawnRateFactor();
            if (leafSettings.considerAsConifer()) {
                modifier = FallingLeavesConfig.CONFIG.coniferLeafSpawnRate.get();
            }
        }
        modifier = modifier / 10f / 75f;
        spawnChance *= modifier;
        spawnChance *= SereneSeasons.getModifier(world);

        while (spawnChance > 0) {
            if (random.nextDouble() < spawnChance) {
                LeafUtil.trySpawnLeafParticle(state, world, pos, random, leafSettings);
            }
            spawnChance -= 1;
        }
    }

}
