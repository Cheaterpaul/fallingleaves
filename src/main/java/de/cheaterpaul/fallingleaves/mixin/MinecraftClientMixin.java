package de.cheaterpaul.fallingleaves.mixin;

import de.cheaterpaul.fallingleaves.util.Wind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @Inject(method = "setLevel", at = @At("HEAD"))
    public void joinWorld(ClientWorld world, CallbackInfo ci) {
        Wind.init();
    }

}
