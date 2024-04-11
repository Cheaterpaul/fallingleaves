package de.cheaterpaul.fallingleaves.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.JsonOps;
import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import de.cheaterpaul.fallingleaves.init.ClientMod;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LeafSettingGenerator implements DataProvider {

    private final PackOutput.PathProvider pathProvider;

    public LeafSettingGenerator(PackOutput packOutput) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "fallingleaves/settings");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        return CompletableFuture.supplyAsync(() -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            BiConsumer<ResourceLocation, LeafSettingsEntry> consumer = (id, entry) -> {
                if (!set.add(id)){
                    throw new IllegalStateException("Duplicate leaf setting entry " + id);
                } else {
                    Path path = this.pathProvider.json(id);
                    list.add(DataProvider.saveStable(cache, LeafSettingsEntry.CODEC, entry, path));
                }
            };
            this.registerLeafSettingEntries(consumer);

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public @NotNull String getName() {
        return "leave settings generator";
    }

    protected void registerLeafSettingEntries(BiConsumer<ResourceLocation, LeafSettingsEntry> consumer) {
        //minecraft
        consumer.accept(new ResourceLocation("minecraft", "spruce_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("minecraft", "jungle_leaves"), new LeafSettingsEntry(1, ClientMod.PALMS));
        consumer.accept(new ResourceLocation("minecraft", "cherry_leaves"), new LeafSettingsEntry(0));
        //byg
        consumer.accept(new ResourceLocation("byg", "blue_spruce_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("byg", "cypress_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("byg", "fir_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("byg", "orange_spruce_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("byg", "pine_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("byg", "red_spruce_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("byg", "yellow_spruce_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("byg", "pink_cherry_leaves"), new LeafSettingsEntry(1.4));
        consumer.accept(new ResourceLocation("byg", "skyris_leaves"), new LeafSettingsEntry(1.4));
        consumer.accept(new ResourceLocation("byg", "white_cherry_leaves"), new LeafSettingsEntry(1.4));
        //terrestria
        consumer.accept(new ResourceLocation("terrestria", "cypress_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("terrestria", "hemlock_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("terrestria", "redwood_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("terrestria", "japenese_maple_shrub_leaves"), new LeafSettingsEntry(0));
        consumer.accept(new ResourceLocation("terrestria", "jungle_palm_leaves"), new LeafSettingsEntry(0.2, ClientMod.PALMS));
        consumer.accept(new ResourceLocation("terrestria", "yucca_palm_leaves"), new LeafSettingsEntry(0.2, ClientMod.PALMS));
        consumer.accept(new ResourceLocation("terrestria", "sakura_leaves"), new LeafSettingsEntry(1.4));
        //traverse
        consumer.accept(new ResourceLocation("traverse", "fir_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("traverse", "brown_autumnal_leaves"), new LeafSettingsEntry(1.8));
        consumer.accept(new ResourceLocation("traverse", "orange_autumnal_leaves"), new LeafSettingsEntry(1.8));
        consumer.accept(new ResourceLocation("traverse", "red_autumnal_leaves"), new LeafSettingsEntry(1.8));
        consumer.accept(new ResourceLocation("traverse", "yellow_autumnal_leaves"), new LeafSettingsEntry(1.8));
        //woods and mires
        consumer.accept(new ResourceLocation("woods_and_mires", "pine_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        //biomes o plenty
        consumer.accept(new ResourceLocation("biomesoplenty", "fir_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("biomesoplenty", "redwood_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("biomesoplenty", "cypress_leaves"), new LeafSettingsEntry(1, ClientMod.CONIFER));
        consumer.accept(new ResourceLocation("biomesoplenty", "snowblossom_leaves"), new LeafSettingsEntry(0));
        consumer.accept(new ResourceLocation("biomesoplenty", "red_maple_leaves"), new LeafSettingsEntry(0));
        consumer.accept(new ResourceLocation("biomesoplenty", "orange_maple_leaves"), new LeafSettingsEntry(0));
        consumer.accept(new ResourceLocation("biomesoplenty", "yellow_maple_leaves"), new LeafSettingsEntry(0));
        consumer.accept(new ResourceLocation("biomesoplenty", "mahogany_leaves"), new LeafSettingsEntry(1));
        consumer.accept(new ResourceLocation("biomesoplenty", "pine_leaves"), new LeafSettingsEntry(1));
        consumer.accept(new ResourceLocation("biomesoplenty", "rainbow_birch_leaves"), new LeafSettingsEntry(1));
        consumer.accept(new ResourceLocation("biomesoplenty", "jacaranda_leaves"), new LeafSettingsEntry(1));
        consumer.accept(new ResourceLocation("biomesoplenty", "palm_leaves"), new LeafSettingsEntry(0.2, ClientMod.PALMS));
        consumer.accept(new ResourceLocation("biomesoplenty", "willow_leaves"), new LeafSettingsEntry(1));
        consumer.accept(new ResourceLocation("biomesoplenty", "dead_leaves"), new LeafSettingsEntry(1));
        consumer.accept(new ResourceLocation("biomesoplenty", "magic_leaves"), new LeafSettingsEntry(1));
        consumer.accept(new ResourceLocation("biomesoplenty", "umbran_leaves"), new LeafSettingsEntry(1));
        consumer.accept(new ResourceLocation("biomesoplenty", "hellbark_leaves"), new LeafSettingsEntry(1));

    }
}
