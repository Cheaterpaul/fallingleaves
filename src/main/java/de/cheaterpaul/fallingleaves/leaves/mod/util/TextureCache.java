package de.cheaterpaul.fallingleaves.leaves.mod.util;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@EventBusSubscriber(modid = FallingLeavesMod.MODID, value = Dist.CLIENT)
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

    // Cache for texture colors
    public static final Map<ResourceLocation, Data> INST = new HashMap<>();

    // Cache for block colors (BlockState + position -> color)
    private static final Map<BlockColorKey, Integer> BLOCK_COLOR_CACHE = new HashMap<>();

    // Cache for combined colors (texture + block color)
    private static final Map<CombinedColorKey, double[]> COMBINED_COLOR_CACHE = new HashMap<>();

    // Key for block color cache
    public record BlockColorKey(BlockState state, BlockPos pos) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BlockColorKey that = (BlockColorKey) o;
            return Objects.equals(state, that.state) && 
                   pos.getX() == that.pos.getX() && 
                   pos.getY() == that.pos.getY() && 
                   pos.getZ() == that.pos.getZ();
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    // Key for combined color cache
    public record CombinedColorKey(ResourceLocation textureId, int blockColor) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CombinedColorKey that = (CombinedColorKey) o;
            return blockColor == that.blockColor && Objects.equals(textureId, that.textureId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(textureId, blockColor);
        }
    }

    private TextureCache() {}

    public static Integer getBlockColor(BlockState state, BlockPos pos) {
        return BLOCK_COLOR_CACHE.get(new BlockColorKey(state, pos));
    }

    public static void putBlockColor(BlockState state, BlockPos pos, int color) {
        BLOCK_COLOR_CACHE.put(new BlockColorKey(state, pos), color);
    }

    public static double[] getCombinedColor(ResourceLocation textureId, int blockColor) {
        return COMBINED_COLOR_CACHE.get(new CombinedColorKey(textureId, blockColor));
    }

    public static void putCombinedColor(ResourceLocation textureId, int blockColor, double[] color) {
        COMBINED_COLOR_CACHE.put(new CombinedColorKey(textureId, blockColor), color);
    }

    @SubscribeEvent
    public static void onReload(TextureAtlasStitchedEvent event) {
        INST.clear();
        BLOCK_COLOR_CACHE.clear();
        COMBINED_COLOR_CACHE.clear();
    }
}
