package de.cheaterpaul.fallingleaves.event.wind;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.wind.IWindLevel;
import de.cheaterpaul.fallingleaves.wind.WindState;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.server.command.EnumArgument;

@EventBusSubscriber(modid = FallingLeavesMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class EventHandler {

    @SubscribeEvent
    public static void on(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal("wind").executes(x -> {
            ((IWindLevel) Minecraft.getInstance().level).fallingLeaves$getWind().changeWind();
            return 0;
        }).then(RequiredArgumentBuilder.<CommandSourceStack,WindState.State>argument("state", EnumArgument.enumArgument(WindState.State.class)).executes(x -> {
            ((IWindLevel) Minecraft.getInstance().level).fallingLeaves$getWind().changeWind(x.getArgument("state", WindState.State.class));
            return 0;
        })));
    }
}
