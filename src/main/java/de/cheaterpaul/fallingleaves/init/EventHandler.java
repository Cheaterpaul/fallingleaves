package de.cheaterpaul.fallingleaves.init;

import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import de.cheaterpaul.fallingleaves.util.LeafUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {
    public EventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Spawn between 0 and 3 leaves on hitting a leaf block
     */
    @SubscribeEvent()
    public void onAttackLeavesBlock(PlayerInteractEvent.LeftClickBlock e) {
        BlockState state = e.getWorld().getBlockState(e.getPos());
        LeafSettingsEntry leafSettings = Config.LEAFSETTINGS.getLeafSetting(state.getBlock().getRegistryName());
        if (leafSettings != null || state.getBlock() instanceof LeavesBlock) {
            // binomial distribution - extremes (0 or 3 leaves) are less likely
            for (int i = 0; i < 3; i++) {
                if (e.getPlayer().getRandom().nextBoolean()) {
                    LeafUtil.trySpawnLeafParticle(state, e.getWorld(), e.getPos(), e.getPlayer().getRandom(), leafSettings);
                }
            }
        }
    }
}
