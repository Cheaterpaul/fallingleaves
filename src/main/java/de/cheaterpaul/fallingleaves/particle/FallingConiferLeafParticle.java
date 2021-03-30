package de.cheaterpaul.fallingleaves.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FallingConiferLeafParticle extends FallingLeafParticle {
    public FallingConiferLeafParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, IAnimatedSprite provider) {
        super(clientWorld, x, y, z, r, g, b, provider);
    }
}
