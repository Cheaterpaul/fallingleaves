package de.cheaterpaul.fallingleaves.mixin.leaves;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.config.Config;
import de.cheaterpaul.fallingleaves.leaves.ILeavesLevel;
import de.cheaterpaul.fallingleaves.leaves.Leaves;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientLevel.class)
public class ClientLevelMixin implements ILeavesLevel {

    @Unique
    private final Leaves fallingLeaves$leaves = new Leaves((ClientLevel) (Object) this);

    @Override
    public Leaves fallingLeaves$getLeaves() {
        return this.fallingLeaves$leaves;
    }
}
