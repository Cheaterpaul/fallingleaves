package de.cheaterpaul.fallingleaves.init;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import de.cheaterpaul.fallingleaves.particle.FallingConiferLeafParticle;
import de.cheaterpaul.fallingleaves.particle.FallingLeafParticle;
import de.cheaterpaul.fallingleaves.util.LeafUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(FallingLeavesMod.MOD_ID)
public class Leaves {

    public static final BasicParticleType falling_leaf = null;
    public static final BasicParticleType falling_leaf_conifer = null;


    @SubscribeEvent
    public void onRegisterParticles(RegistryEvent.Register<ParticleType<?>> event) {
        event.getRegistry().register(new BasicParticleType(false).setRegistryName(modId("falling_leaf")));
        event.getRegistry().register(new BasicParticleType(false).setRegistryName(modId("falling_leaf_conifer")));
    }

    public static ResourceLocation modId(String name) {
        return new ResourceLocation(FallingLeavesMod.MOD_ID, name);
    }

    public static void registerParticles() {
        Minecraft.getInstance().particleEngine.register(falling_leaf, FallingLeafParticle.DefaultFactory::new);
        Minecraft.getInstance().particleEngine.register(falling_leaf_conifer, FallingConiferLeafParticle.DefaultFactory::new);
    }

    /** Spawn between 0 and 3 leaves on hitting a leaf block */
    @SubscribeEvent
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
