package de.cheaterpaul.fallingleaves.leaves.mod.util;

import com.mojang.blaze3d.platform.NativeImage;
import de.cheaterpaul.fallingleaves.mixin.NativeImageAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

public class LeafHelper {

    private static final Logger LOGGER = LogManager.getLogger();

    public static double[] getBlockTextureColor(BlockState state, ClientLevel level, BlockPos pos) {
        BlockStateModel blockModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);

        var quads = blockModel.collectParts(level, pos, state, level.random).stream().flatMap(x -> x.getQuads(Direction.DOWN).stream()).toList();

        TextureAtlasSprite sprite;
        boolean shouldColor;
        if (quads.isEmpty()) {
            sprite = blockModel.particleIcon(level, pos, state);
            shouldColor = true;
        } else {
            BakedQuad quad = quads.getFirst();
            sprite = quad.sprite();
            shouldColor = quad.isTinted();
        }

        SpriteContents contents = sprite.contents();
        ResourceLocation name = contents.name();
        NativeImage nativeImage = contents.byMipLevel[0];
        int blockColor = (shouldColor ? Minecraft.getInstance().getBlockColors().getColor(state, level, pos, 0) : -1);

        return calculateLeafColor(name, nativeImage, blockColor);
    }

    private static double[] calculateLeafColor(ResourceLocation spriteId, NativeImage texture, int blockColor) {
        double[] textureColor = TextureCache.INST.computeIfAbsent(spriteId, (loc) -> {
            double[] doubles = averageColor(texture);
            LogManager.getLogger().debug("{}: Calculated texture color {} ", spriteId, doubles);
            return new TextureCache.Data(doubles);
        }).getColor();

        if (blockColor != -1) {
            // multiply texture and block color RGB values (in range 0-1)
            textureColor[0] *= (blockColor >> 16 & 255) / 255.0;
            textureColor[1] *= (blockColor >> 8  & 255) / 255.0;
            textureColor[2] *= (blockColor       & 255) / 255.0;
        }

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
