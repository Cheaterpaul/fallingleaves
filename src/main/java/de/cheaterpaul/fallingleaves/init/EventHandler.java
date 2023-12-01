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

package de.cheaterpaul.fallingleaves.init;

import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import de.cheaterpaul.fallingleaves.util.LeafUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class EventHandler {

    public EventHandler() {
        NeoForge.EVENT_BUS.register(this);
    }

    /**
     * Spawn between 0 and 3 leaves on hitting a leaf block
     */
    @SubscribeEvent
    public void onAttackLeavesBlock(PlayerInteractEvent.LeftClickBlock e) {
        if (e.getLevel().isClientSide) {
            BlockState state = e.getLevel().getBlockState(e.getPos());
            LeafSettingsEntry leafSettings = ClientMod.getLeafSetting(BuiltInRegistries.BLOCK.getKey(state.getBlock()));
            if (leafSettings != null || state.getBlock() instanceof LeavesBlock) {
                // binomial distribution - extremes (0 or 3 leaves) are less likely
                for (int i = 0; i < 3; i++) {
                    if (e.getEntity().getRandom().nextBoolean()) {
                        LeafUtil.trySpawnLeafParticle(state, e.getLevel(), e.getPos(), e.getEntity().getRandom(), leafSettings);
                    }
                }
            }
        }
    }
}
