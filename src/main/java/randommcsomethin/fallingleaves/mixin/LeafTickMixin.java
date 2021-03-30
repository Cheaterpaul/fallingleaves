package randommcsomethin.fallingleaves.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;
import randommcsomethin.fallingleaves.init.Config;

import java.util.Random;

import static randommcsomethin.fallingleaves.util.LeafUtil.trySpawnLeafParticle;

@OnlyIn(Dist.CLIENT)
@Mixin(LeavesBlock.class)
public abstract class LeafTickMixin {

    @Inject(at = @At("HEAD"), method = "randomTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V")
    private void randomDisplayTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        LeafSettingsEntry leafSettings = Config.LEAFSETTINGS.getLeafSetting(state.getBlock().getRegistryName());

        // Every leaf block has a settings entry, but some blocks are considered leaves when they technically aren't
        // E.g. terrestria:sakura_log can be "leaf-logged" - in that case, we simply ignore them
        if (leafSettings == null && !(state.getBlock() instanceof LeavesBlock))
            return;

        if (!Config.CONFIG.dropFromPlayerPlacedBlocks.get() && state.getValue(LeavesBlock.PERSISTENT))
            return;

        double spawnChance = leafSettings == null? 1:leafSettings.getSpawnChance();

        if (spawnChance != 0 && random.nextDouble() < spawnChance) {
            trySpawnLeafParticle(state, world, pos, random, leafSettings);
        }
    }

}
