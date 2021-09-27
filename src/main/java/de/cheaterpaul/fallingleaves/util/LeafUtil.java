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

package de.cheaterpaul.fallingleaves.util;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import de.cheaterpaul.fallingleaves.init.FallingLeavesConfig;
import de.cheaterpaul.fallingleaves.init.Leaves;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class LeafUtil {

    public static void trySpawnLeafParticle(BlockState state, World world, BlockPos pos, Random random, @Nullable LeafSettingsEntry leafSettings) {
        // Particle position
        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() - (random.nextDouble() / 3);
        double z = pos.getZ() + random.nextDouble();

        if (shouldSpawnParticle(world, pos, x, y, z)) {
            Minecraft client = Minecraft.getInstance();

            // read the bottom quad to determine whether we should color the texture
            IBakedModel model = client.getBlockRenderer().getBlockModel(state);
            IModelData modelData = model.getModelData(world, pos, state, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
            List<BakedQuad> quads = model.getQuads(state, Direction.DOWN, random, modelData);
            boolean shouldColor = quads.isEmpty() || quads.stream().anyMatch(BakedQuad::isTinted);

            int blockColor = client.getBlockColors().getColor(state, world, pos, 0);
            ResourceLocation texture = spriteToTexture(client.getModelManager().getBlockModelShaper().getTexture(state, world, pos));

            double[] color = calculateLeafColor(texture, shouldColor, blockColor, client);

            double r = color[0];
            double g = color[1];
            double b = color[2];

            // Add the particle.
            world.addParticle(leafSettings == null || !leafSettings.isConiferBlock ? Leaves.falling_leaf:Leaves.falling_leaf_conifer, x, y, z, r, g, b);
        }
    }

    private static double[] calculateLeafColor(ResourceLocation texture, boolean shouldColor, int blockColor, Minecraft client) {
        try (IResource res = client.getResourceManager().getResource(texture)) {
            String resourcePack = res.getSourceName();
            TextureCache.Data cache = TextureCache.INST.get(texture);
            double[] textureColor;

            // only use cached texture color when resourcePack matches
            if (cache != null && resourcePack.equals(cache.resourcePack)) {
                textureColor = cache.getColor();
            } else {
                // read and cache texture color
                try (InputStream is = res.getInputStream()) {
                    textureColor = averageColor(ImageIO.read(is));
                    TextureCache.INST.put(texture, new TextureCache.Data(textureColor, resourcePack));
                    FallingLeavesMod.LOGGER.debug("{}: Calculated texture color {} ", texture, textureColor);
                }
            }

            if (shouldColor && blockColor != -1) {
                // multiply texture and block color RGB values (in range 0-1)
                textureColor[0] *= (blockColor >> 16 & 255) / 255.0;
                textureColor[1] *= (blockColor >> 8  & 255) / 255.0;
                textureColor[2] *= (blockColor       & 255) / 255.0;
            }

            return textureColor;
        } catch (IOException e) {
            FallingLeavesMod.LOGGER.error("Couldn't access resource {}", texture, e);
            return new double[] { 1, 1, 1 };
        }
    }

    private static boolean shouldSpawnParticle(World world, BlockPos pos, double x, double y, double z) {
        // Never spawn a particle if there's a leaf block below
        // This test is necessary because modded leaf blocks may not have collisions
        if (isLeafBlock(world.getBlockState(pos.below()).getBlock(), true)) return false;

        double y2 = y - FallingLeavesConfig.CONFIG.minimumFreeSpaceBelow.get() * 0.5;
        AxisAlignedBB collisionBox = new AxisAlignedBB(x - 0.1, y, z - 0.1, x + 0.1, y2, z + 0.1);

        // Only spawn the particle if there's enough room for it
        return !world.getBlockCollisions(null, collisionBox).findAny().isPresent();
    }

    /** Block tags can only be used once the integrated server is started */
    public static boolean isLeafBlock(Block block, boolean useBlockTags) {
        return (block instanceof LeavesBlock) || (useBlockTags && block.is(BlockTags.LEAVES));
    }

    public static double[] averageColor(BufferedImage image) {
        double r = 0;
        double g = 0;
        double b = 0;
        int n = 0;

        // TODO: This entire block feels like it could be simplified or broken down into
        //       more manageable parts.
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color c = new Color(image.getRGB(x, y), true);

                // Only take completely opaque pixels into account
                if (c.getAlpha() == 255) {
                    r += c.getRed();
                    g += c.getGreen();
                    b += c.getBlue();
                    n++;
                }
            }
        }

        return new double[] {
                (r / n) / 255.0,
                (g / n) / 255.0,
                (b / n) / 255.0
        };
    }

    public static ResourceLocation spriteToTexture(TextureAtlasSprite sprite) {
        String texture = sprite.getName().getPath(); // e.g. block/sakura_leaves
        return new ResourceLocation(sprite.getName().getNamespace(), "textures/" + texture + ".png");
    }

}
