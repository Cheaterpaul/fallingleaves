package de.cheaterpaul.fallingleaves.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public class TextureCache {
    public static final class Data {
        private final double[] color;
        public final String resourcePack;

        public Data(double[] color, String resourcePack) {
            if (color.length != 3)
                throw new IllegalArgumentException("texture color should have 3 components");

            this.color = new double[3];
            System.arraycopy(color, 0, this.color, 0, 3);
            this.resourcePack = resourcePack;
        }

        public double[] getColor() {
            double[] copy = new double[3];
            System.arraycopy(color, 0, copy, 0, 3);
            return copy;
        }
    }

    public static final HashMap<ResourceLocation, Data> INST = new HashMap<>();

    private TextureCache() {}

}
