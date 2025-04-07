package de.cheaterpaul.fallingleaves.mixin.wind;

import de.cheaterpaul.fallingleaves.wind.IWindLevel;
import de.cheaterpaul.fallingleaves.wind.Wind;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ClientLevel.class)
public class ClientLevelMixin implements IWindLevel {

    @Unique
    private final Wind fallingLeaves$wind = new Wind((ClientLevel) (Object) this);

    @Override
    public Wind fallingLeaves$getWind() {
        return this.fallingLeaves$wind;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        this.fallingLeaves$wind.tick();
    }
}
