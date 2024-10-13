package de.cheaterpaul.fallingleaves.data;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class LeafSettingGenerator implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> holderLookup;

    public LeafSettingGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> holderLookup) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "fallingleaves/settings");
        this.holderLookup = holderLookup;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        return this.holderLookup.thenCompose((holder) -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            BiConsumer<ResourceLocation, LeafSettingsEntry> consumer = (id, entry) -> {
                if (!set.add(id)){
                    throw new IllegalStateException("Duplicate leaf setting entry " + id);
                } else {
                    Path path = this.pathProvider.json(id);
                    list.add(DataProvider.saveStable(cache, holder, LeafSettingsEntry.CODEC, entry, path));
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
        consumer.accept(ResourceLocation.fromNamespaceAndPath("minecraft", "spruce_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("minecraft", "jungle_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.DEFAULT));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("minecraft", "cherry_leaves"), new LeafSettingsEntry(0));
        //byg
        consumer.accept(ResourceLocation.fromNamespaceAndPath("byg", "blue_spruce_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("byg", "cypress_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("byg", "fir_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("byg", "orange_spruce_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("byg", "pine_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("byg", "red_spruce_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("byg", "yellow_spruce_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("byg", "pink_cherry_leaves"), new LeafSettingsEntry(1.4));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("byg", "skyris_leaves"), new LeafSettingsEntry(1.4));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("byg", "white_cherry_leaves"), new LeafSettingsEntry(1.4));
        //terrestria
        consumer.accept(ResourceLocation.fromNamespaceAndPath("terrestria", "cypress_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("terrestria", "hemlock_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("terrestria", "redwood_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("terrestria", "japanese_maple_shrub_leaves"), new LeafSettingsEntry(0, FallingLeavesMod.MAPLE));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("terrestria", "jungle_palm_leaves"), new LeafSettingsEntry(0.2, FallingLeavesMod.PALMS));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("terrestria", "yucca_palm_leaves"), new LeafSettingsEntry(0.2, FallingLeavesMod.PALMS));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("terrestria", "sakura_leaves"), new LeafSettingsEntry(1.4));
        //traverse
        consumer.accept(ResourceLocation.fromNamespaceAndPath("traverse", "fir_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("traverse", "brown_autumnal_leaves"), new LeafSettingsEntry(1.8));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("traverse", "orange_autumnal_leaves"), new LeafSettingsEntry(1.8));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("traverse", "red_autumnal_leaves"), new LeafSettingsEntry(1.8));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("traverse", "yellow_autumnal_leaves"), new LeafSettingsEntry(1.8));
        //woods and mires
        consumer.accept(ResourceLocation.fromNamespaceAndPath("woods_and_mires", "pine_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        //biomes o plenty
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "fir_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "redwood_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "cypress_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.CONIFER));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "snowblossom_leaves"), new LeafSettingsEntry(0));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "red_maple_leaves"), new LeafSettingsEntry(0, FallingLeavesMod.MAPLE));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "orange_maple_leaves"), new LeafSettingsEntry(0, FallingLeavesMod.MAPLE));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "yellow_maple_leaves"), new LeafSettingsEntry(0, FallingLeavesMod.MAPLE));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "mahogany_leaves"), new LeafSettingsEntry(1, FallingLeavesMod.MAHOGANY));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "pine_leaves"), new LeafSettingsEntry(1));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "rainbow_birch_leaves"), new LeafSettingsEntry(1));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "jacaranda_leaves"), new LeafSettingsEntry(1));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "palm_leaves"), new LeafSettingsEntry(0.2, FallingLeavesMod.PALMS));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "willow_leaves"), new LeafSettingsEntry(1));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "dead_leaves"), new LeafSettingsEntry(1));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "magic_leaves"), new LeafSettingsEntry(1));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "umbran_leaves"), new LeafSettingsEntry(1));
        consumer.accept(ResourceLocation.fromNamespaceAndPath("biomesoplenty", "hellbark_leaves"), new LeafSettingsEntry(1));

    }
}
