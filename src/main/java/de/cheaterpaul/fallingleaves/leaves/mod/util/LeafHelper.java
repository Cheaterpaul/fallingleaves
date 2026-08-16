package de.cheaterpaul.fallingleaves.leaves.mod.util;

import com.mojang.blaze3d.platform.NativeImage;
import de.cheaterpaul.fallingleaves.mixin.NativeImageAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;

public class LeafHelper {

    private static final Logger LOGGER = LogManager.getLogger();

    public static double[] getBlockTextureColor(BlockState state, ClientLevel level, BlockPos pos) {
        BlockStateModel blockModel = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);

        var list = new ArrayList<BlockStateModelPart>();
        blockModel.collectParts(level, pos, state, level.getRandom(), list);
        var quads = list.stream().flatMap(x -> x.getQuads(Direction.DOWN).stream()).toList();

        TextureAtlasSprite sprite;
        boolean shouldColor;
        if (quads.isEmpty()) {
            sprite = blockModel.particleMaterial(level, pos, state).sprite();
            shouldColor = true;
        } else {
            var material = quads.getFirst().materialInfo();
            sprite = material.sprite();
            shouldColor = material.isTinted();
        }

        SpriteContents contents = sprite.contents();
        Identifier name = contents.name();

        // Get block color from cache or calculate it
        Integer cachedBlockColor = TextureCache.getBlockColor(state, pos);
        int blockColor;

        if (cachedBlockColor != null) {
            blockColor = cachedBlockColor;
        } else {
            blockColor = (shouldColor ? level.getBlockTint(pos, BiomeColors.FOLIAGE_COLOR_RESOLVER) : -1);
            TextureCache.putBlockColor(state, pos, blockColor);
        }

        return calculateLeafColor(name, contents.byMipLevel[0], blockColor);
    }

    private static double[] calculateLeafColor(Identifier spriteId, NativeImage texture, int blockColor) {
        // Check if we already have the combined color cached
        double[] cachedCombinedColor = TextureCache.getCombinedColor(spriteId, blockColor);
        if (cachedCombinedColor != null) {
            return cachedCombinedColor;
        }

        // Get texture color from cache or calculate it
        double[] textureColor = TextureCache.INST.computeIfAbsent(spriteId, (loc) -> {
            double[] doubles = averageColor(texture);
            LogManager.getLogger().debug("{}: Calculated texture color {} ", spriteId, doubles);
            return new TextureCache.Data(doubles);
        }).getColor();

        // Apply block tint if needed
        if (blockColor != -1) {
            // multiply texture and block color RGB values (in range 0-1)
            textureColor[0] *= (blockColor >> 16 & 255) / 255.0;
            textureColor[1] *= (blockColor >> 8  & 255) / 255.0;
            textureColor[2] *= (blockColor       & 255) / 255.0;
        }

        // Cache the combined color
        TextureCache.putCombinedColor(spriteId, blockColor, textureColor);

        return textureColor;
    }

    public static double[] averageColor(NativeImage image) {
        if (image.format() != NativeImage.Format.RGBA) {
            LOGGER.error("RGBA image required, was {}", image.format());
            return new double[] {1, 1, 1};
        }

        long pixels = ((NativeImageAccessor) (Object)image).getPixels();

        if (pixels == 0) {
            LOGGER.error("image is not allocated");
            return new double[] {1, 1, 1};
        }

        double r = 0;
        double g = 0;
        double b = 0;
        int n = 0;

        int width = image.getWidth();
        int height = image.getHeight();

        // add up all opaque color values (this variant is much faster than using image.getPixelColor(x, y))
        for (int i = 0; i < width * height; i++) {
            int c = MemoryUtil.memGetInt(pixels + 4L * i);

            int cr = (c       & 255);
            int cg = (c >> 8  & 255);
            int cb = (c >> 16 & 255);
            int ca = (c >> 24 & 255);

            if (ca != 0) {
                r += cr;
                g += cg;
                b += cb;
                n++;
            }
        }

        return new double[] {
                (r / n) / 255.0,
                (g / n) / 255.0,
                (b / n) / 255.0
        };
    }
}
