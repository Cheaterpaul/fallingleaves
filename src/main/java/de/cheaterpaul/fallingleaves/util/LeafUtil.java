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

import com.mojang.blaze3d.platform.NativeImage;
import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import de.cheaterpaul.fallingleaves.init.ClientMod;
import de.cheaterpaul.fallingleaves.init.FallingLeavesConfig;
import de.cheaterpaul.fallingleaves.mixin.NativeImageAccessor;
import de.cheaterpaul.fallingleaves.particle.FallingLeafParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;
import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class LeafUtil {

    private static final Logger LOGGER = LogManager.getLogger(FallingLeavesMod.class);
    private static final RandomSource renderRandom = RandomSource.create();
    private static final FallingLeafParticle.LeavesParticleFactory factory = new FallingLeafParticle.LeavesParticleFactory();

    public static void trySpawnLeafParticle(BlockState state, Level world, BlockPos pos, RandomSource random, @Nullable LeafSettingsEntry leafSettings) {
        // Particle position
        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() - (random.nextDouble() / 3);
        double z = pos.getZ() + random.nextDouble();

        if (shouldSpawnParticle(world, pos, x, y, z)) {
            Minecraft client = Minecraft.getInstance();

            // read the bottom quad to determine whether we should color the texture
            BakedModel model = client.getBlockRenderer().getBlockModel(state);
            ModelData modelData = model.getModelData(world, pos, state, ModelData.EMPTY);

            double[] color = getBlockTextureColor(state, world, pos, modelData);

            double r = color[0];
            double g = color[1];
            double b = color[2];

            // Add the particle.
            var particle = factory.createParticle(null, (ClientLevel) world, x, y, z, r, g, b, getSpriteSetForSettings(state, leafSettings));
            if (particle != null) {
                Minecraft.getInstance().particleEngine.add(particle);
            }
        }
    }

    private static SpriteSet getSpriteSetForSettings(BlockState blockState, @Nullable LeafSettingsEntry entry) {
        var set = ClientMod.getSpriteForLeafType(entry == null ? ForgeRegistries.BLOCKS.getKey(blockState.getBlock()) : entry.leafType());
        if (set == null) {
            set = ClientMod.getSpriteForLeafType(entry == null || !entry.considerAsConifer()? ClientMod.DEFAULT : ClientMod.CONIFER);
        }
        return set;
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

            // RGBA format
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

    public static double[] getBlockTextureColor(BlockState state, Level world, BlockPos pos, ModelData modelData) {
        Minecraft client = Minecraft.getInstance();
        BakedModel model = client.getBlockRenderer().getBlockModel(state);

        renderRandom.setSeed(state.getSeed(pos));
        List<BakedQuad> quads = model.getQuads(state, Direction.DOWN, renderRandom, modelData, RenderType.cutout());

        TextureAtlasSprite sprite;
        boolean shouldColor;

        // read data from the first bottom quad if possible
        if (!quads.isEmpty()) {
            BakedQuad quad = quads.get(0);
            sprite = quad.getSprite();
            shouldColor = quad.isTinted();
        } else {
            // fall back to block breaking particle
            sprite = model.getParticleIcon(modelData);
            shouldColor = true;
        }

        SpriteContents contents = sprite.contents();
        ResourceLocation spriteId = contents.name();
        NativeImage texture = contents.byMipLevel[0]; // directly extract texture
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
