package de.cheaterpaul.fallingleaves.leaves.mod.util;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = FallingLeavesMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TextureCache {
    public record Data(double[] color) {

        public Data {
            if (color.length != 3)
                throw new IllegalArgumentException("texture color should have 3 components");
        }

        public double[] getColor() {
            return Arrays.copyOf(color, color.length);
        }
    }

    public static final Map<ResourceLocation, Data> INST = new HashMap<>();

    private TextureCache() {}

    @SubscribeEvent
    public static void onReload(TextureAtlasStitchedEvent event) {
        INST.clear();
    }
}
