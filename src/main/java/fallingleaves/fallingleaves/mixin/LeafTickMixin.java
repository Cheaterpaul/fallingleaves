package fallingleaves.fallingleaves.mixin;

import fallingleaves.fallingleaves.LeafUtils;
import fallingleaves.fallingleaves.LeafUtils;
import fallingleaves.fallingleaves.client.FallingLeavesClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static fallingleaves.fallingleaves.client.FallingLeavesClient.FALLING_LEAF;
import static fallingleaves.fallingleaves.client.FallingLeavesClient.FALLING_SPRUCE_LEAF;

@Environment(EnvType.CLIENT)
@Mixin(LeavesBlock.class)
public abstract class LeafTickMixin {
    @Unique
    private static final HashMap<String, Integer> textureColor = new HashMap<>();

    @Inject(at = @At("HEAD"), method = "randomDisplayTick")
    private void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo info) {
        double rateVar = 1.0;
        for (int leaf = 0; leaf < FallingLeavesClient.coniferLeaves.length; leaf++) {
            if (state.getBlock() == FallingLeavesClient.coniferLeaves[leaf])
                rateVar = FallingLeavesClient.config.coniferLeafRate;
                    else rateVar = FallingLeavesClient.config.leafRate;
        }
        if (rateVar != 0 && random.nextInt((int) (75*rateVar)) == 0) {
            Direction direction = Direction.DOWN;
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (!(!blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite()) && !blockState.isTranslucent(world, blockPos) && !blockState.isSolidBlock(world, blockPos))) {
                double d = direction.getOffsetX() == 0 ? random.nextDouble() : 0.5D + (double) direction.getOffsetX() * 0.6D;
                double f = direction.getOffsetZ() == 0 ? random.nextDouble() : 0.5D + (double) direction.getOffsetZ() * 0.6D;

                MinecraftClient client = MinecraftClient.getInstance();
                int j = client.getBlockColors().getColor(state, world, blockPos.offset(Direction.UP), 0);
                if (j == -1) {
                    String texture = LeafUtils.spriteToTexture(client.getBlockRenderManager().getModel(state).getSprite());
                    Integer color = textureColor.get(texture);

                    if (color != null) {
                        j = color;
                    } else {
                        try {
                            try (InputStream is = client.getResourceManager().getResource(new Identifier(texture)).getInputStream()) {
                                BufferedImage img = ImageIO.read(is);
                                j = LeafUtils.averageColor(img, img.getWidth(), img.getHeight()).getRGB();
                                textureColor.put(texture, j); //this shouldn't get too large, right?
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                /*
                if (j == -1) {
                    List<BakedQuad> quads = MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState).getQuads(blockState, Direction.DOWN, random);
                    for (int i = 0; i < Direction.values().length; i++) {
                        Direction[] dirz = Direction.values();
                        quads = MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState).getQuads(blockState, dirz[i], random);
                        System.out.println("Getting quad...");
                        if (!quads.isEmpty()) {
                            System.out.println("Quad found!");
                            if (quads.get(quads.size() - 1).hasColor()) {
                                System.out.println("Quad has color");
                                break;
                            }
                        }
                    }
                    if (!quads.isEmpty()) {
                        j = MinecraftClient.getInstance().getBlockColors().getColor(state, world, blockPos, quads.get(quads.size() - 1).getColorIndex());
                        System.out.println("Color is " + j);
                    }
                    j = state.getMaterial().getColor().color;
                    System.out.println("Default color is " + j);
                }
                */
                //if (j == 16777215) {
                //}
                float k = (float) (j >> 16 & 255) / 255.0F;
                float l = (float) (j >> 8 & 255) / 255.0F;
                float m = (float) (j & 255) / 255.0F;

                //Regular leaves
                boolean isConifer = false;
                for (Block b : FallingLeavesClient.coniferLeaves) {
                    if (state.getBlock() == b) {
                        isConifer = true;
                        break;
                    }
                }
                world.addParticle(isConifer ? FALLING_SPRUCE_LEAF : FALLING_LEAF, (double)pos.getX() + d, pos.getY(), (double)pos.getZ() + f, k, l, m);

                //Dynamic leaves
                /*
                if (world.isClient) {
                    new DynamicLeafParticle((ClientWorld) world, (double) pos.getX() + d, pos.getY(), (double) pos.getZ() + f, k, l, m, state);
                }
                 */
            }
        }
    }
}
