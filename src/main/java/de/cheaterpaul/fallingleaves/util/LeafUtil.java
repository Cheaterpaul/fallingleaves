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

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.NativeImage;
import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import de.cheaterpaul.fallingleaves.init.FallingLeavesConfig;
import de.cheaterpaul.fallingleaves.init.Leaves;
import de.cheaterpaul.fallingleaves.mixin.TextureAtlasSpriteAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class LeafUtil {

    private static final RandomSource renderRandom = RandomSource.create();

    public static void trySpawnLeafParticle(BlockState state, Level world, BlockPos pos, RandomSource random, @Nullable LeafSettingsEntry leafSettings) {
        // Particle position
        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() - (random.nextDouble() / 3);
        double z = pos.getZ() + random.nextDouble();

        if (shouldSpawnParticle(world, pos, x, y, z)) {
            Minecraft client = Minecraft.getInstance();

            // read the bottom quad to determine whether we should color the texture
            BakedModel model = client.getBlockRenderer().getBlockModel(state);
            IModelData modelData = model.getModelData(world, pos, state, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
            List<BakedQuad> quads = model.getQuads(state, null, random, modelData);
            boolean shouldColor = quads.isEmpty() || quads.stream().anyMatch(BakedQuad::isTinted);

            int blockColor = client.getBlockColors().getColor(state, world, pos, 0);
            ResourceLocation texture = spriteToTexture(client.getModelManager().getBlockModelShaper().getTexture(state, world, pos));

            double[] color = getBlockTextureColor(state, world, pos);

            double r = color[0];
            double g = color[1];
            double b = color[2];

            // Add the particle.
            ParticleProvider<?> provicer = leafSettings == null || !leafSettings.isConiferBlock ?Leaves.falling_leaf:Leaves.falling_leaf_conifer;
            var particle = provicer.createParticle(null,(ClientLevel) world,x, y, z, r, g, b );
            if (particle != null) {
                Minecraft.getInstance().particleEngine.add(particle);
            }
        }
    }

    private static boolean shouldSpawnParticle(Level world, BlockPos pos, double x, double y, double z) {
        // Never spawn a particle if there's a leaf block below
        // This test is necessary because modded leaf blocks may not have collisions
        if (isLeafBlock(world.getBlockState(pos.below()), true)) return false;

        double y2 = y - FallingLeavesConfig.CONFIG.minimumFreeSpaceBelow.get() * 0.5;
        AABB collisionBox = new AABB(x - 0.1, y, z - 0.1, x + 0.1, y2, z + 0.1);

        // Only spawn the particle if there's enough room for it
        return !world.getBlockCollisions(null, collisionBox).iterator().hasNext();
    }

    /** Block tags can only be used once the integrated server is started */
    public static boolean isLeafBlock(BlockState block, boolean useBlockTags) {
        return (block.getBlock() instanceof LeavesBlock) || (useBlockTags && block.is(BlockTags.LEAVES));
    }

    public static double[] averageColor(NativeImage image) {
        double r = 0;
        double g = 0;
        double b = 0;
        int n = 0;

        // TODO: This entire block feels like it could be simplified or broken down into
        //       more manageable parts.
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color c = new Color(image.getPixelRGBA(x, y), true);

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

    public static double[] getBlockTextureColor(BlockState state, Level world, BlockPos pos) {
        Minecraft client = Minecraft.getInstance();
        BakedModel model = client.getBlockRenderer().getBlockModel(state);

        renderRandom.setSeed(state.getSeed(pos));
        List<BakedQuad> quads = model.getQuads(state, Direction.DOWN, renderRandom);

        TextureAtlasSprite sprite;
        boolean shouldColor;

        // read data from the first bottom quad if possible
        if (!quads.isEmpty()) {
            boolean useFirstQuad = true;

            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
            if (id.getNamespace().equals("byg")) {
                /*
                 * some BYG leaves have their actual tinted leaf texture in an "overlay" that comes second, full list:
                 * flowering_orchard_leaves, joshua_leaves, mahogany_leaves, maple_leaves, orchard_leaves,
                 * rainbow_eucalyptus_leaves, ripe_joshua_leaves, ripe_orchard_leaves, willow_leaves
                 */
                useFirstQuad = false;
            }

            BakedQuad quad = quads.get(useFirstQuad ? 0 : quads.size() - 1);
            sprite = quad.getSprite();
            shouldColor = quad.isTinted();
        } else {
            // fall back to block breaking particle
            sprite = model.getParticleIcon();
            shouldColor = true;
        }

        ResourceLocation spriteId = sprite.getName();
        NativeImage texture = ((TextureAtlasSpriteAccessor) sprite).getMainImage()[0]; // directly extract texture
        int blockColor = (shouldColor ? client.getBlockColors().getColor(state, world, pos, 0) : -1);

        return calculateLeafColor(spriteId, texture, blockColor);
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

}
