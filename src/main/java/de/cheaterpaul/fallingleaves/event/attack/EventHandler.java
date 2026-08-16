package de.cheaterpaul.fallingleaves.event.attack;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.leaves.ILeavesLevel;
import de.cheaterpaul.fallingleaves.snow.ISnowLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = FallingLeavesMod.MODID, value = Dist.CLIENT)
public class EventHandler {

    @SubscribeEvent
    public static void onBlockAttack(PlayerInteractEvent.LeftClickBlock event) {
        if (!(event.getLevel() instanceof ClientLevel)) return;
        BlockPos pos = event.getPos();
        BlockState blockState = event.getLevel().getBlockState(pos);
        if (!blockState.is(BlockTags.LEAVES)) return;
        
        BlockPos posBelow = pos.below();
        BlockState statebelow = event.getLevel().getBlockState(posBelow);

        if (event.getLevel() instanceof ILeavesLevel leavesLevel) {
            leavesLevel.fallingLeaves$getLeaves().makeAttackLeavesParticles((ClientLevel) event.getLevel(), pos, blockState, event.getLevel().getRandom(), posBelow, statebelow);
        }
        if (event.getLevel() instanceof ISnowLevel snowLevel) {
            snowLevel.fallingLeaves$getSnow().makeAttackSnowParticles((ClientLevel) event.getLevel(), pos, blockState, event.getLevel().getRandom(), posBelow, statebelow);
        }
    }
}
