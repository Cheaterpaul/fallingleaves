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
