package de.cheaterpaul.fallingleaves.mixin.snow;

import de.cheaterpaul.fallingleaves.snow.ISnowLevel;
import de.cheaterpaul.fallingleaves.snow.Snow;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientLevel.class)
public class ClientLevelMixin implements ISnowLevel {

    @Unique
    Snow fallingLeaves$snow = new Snow();

    @Override
    public Snow fallingLeaves$getSnow() {
        return this.fallingLeaves$snow;
    }
}
