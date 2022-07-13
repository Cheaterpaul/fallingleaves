package de.cheaterpaul.fallingleaves.init;

import de.cheaterpaul.fallingleaves.data.LeafSettingGenerator;
import de.cheaterpaul.fallingleaves.modcompat.SereneSeasons;
import de.cheaterpaul.fallingleaves.particle.FallingConiferLeafParticle;
import de.cheaterpaul.fallingleaves.particle.FallingLeafParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientMod {
    private static void gatherData(final GatherDataEvent event) {
        if (event.includeClient()) {
            event.getGenerator().addProvider(event.includeServer(), new LeafSettingGenerator(event.getGenerator()));
        }
    }

    public static void setupClient(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ClientMod::gatherData);
        bus.addListener(ClientMod::onTextureAtlasPre);
        bus.addListener(ClientMod::onTextureAtlasPost);
        if (SereneSeasons.setup()) {
            bus.register(SereneSeasons.class);
        }
        FallingLeavesConfig.registerConfigs();
    }

    public static void onTextureAtlasPre(TextureStitchEvent.Pre event){
        if (event.getAtlas().location().equals(TextureAtlas.LOCATION_PARTICLES)) {
            FallingLeafParticle.getTextures().forEach(event::addSprite);
            FallingConiferLeafParticle.getTextures().forEach(event::addSprite);
        }
    }

    public static void onTextureAtlasPost(TextureStitchEvent.Post event){
        if (event.getAtlas().location().equals(TextureAtlas.LOCATION_PARTICLES)) {
            Leaves.falling_leaf.rebind(FallingLeafParticle.getTextures().stream().map(loc -> event.getAtlas().getSprite(loc)).toList());
            Leaves.falling_leaf_conifer.rebind(FallingConiferLeafParticle.getTextures().stream().map(loc -> event.getAtlas().getSprite(loc)).toList());
        }
    }
}
