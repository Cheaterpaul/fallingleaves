package de.cheaterpaul.fallingleaves.data;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.data.generator.LeafSettingGenerator;
import de.cheaterpaul.fallingleaves.data.generator.LeafTypeGenerator;
import de.cheaterpaul.fallingleaves.data.provider.LeafProvider;
import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafSetting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = FallingLeavesMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LeafLoader {

    private static LeafProvider LEAF_PROVIDER;
    public static final ResourceLocation DEFAULT_SETTINGS = ResourceLocation.fromNamespaceAndPath("minecraft", "oak_leaves");

    @SubscribeEvent
    public static void registerReloadListener(AddClientReloadListenersEvent event) {
        event.addListener(LeafProvider.LEAF_LISTENER, LEAF_PROVIDER = new LeafProvider());
    }

    public static LeafSetting.LoadedLeafSetting getDefault(){
        return LEAF_PROVIDER.getLoadedSettings().get(DEFAULT_SETTINGS);
    }

    public static LeafSetting.LoadedLeafSetting get(Block block) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
        return LEAF_PROVIDER.getLoadedSettings().get(key);
    }

    public static LeafSetting.LoadedLeafSetting getOrDefault(Block block) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
        if (LEAF_PROVIDER.getLoadedSettings().containsKey(key)) {
            return LEAF_PROVIDER.getLoadedSettings().get(key);
        } else {
            return getDefault();
        }

    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.getGenerator().addProvider(true, new LeafSettingGenerator(event.getGenerator().getPackOutput(), event.getLookupProvider()));
        event.getGenerator().addProvider(true, new LeafTypeGenerator(event.getGenerator().getPackOutput(), event.getLookupProvider()));
    }


}
