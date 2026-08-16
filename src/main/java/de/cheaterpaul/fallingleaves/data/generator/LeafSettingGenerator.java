package de.cheaterpaul.fallingleaves.data.generator;

import de.cheaterpaul.fallingleaves.data.LeafLoader;
import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafSetting;
import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
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
            Set<Identifier> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            BiConsumer<Identifier, LeafSetting> consumer = (id, entry) -> {
                if (!set.add(id)){
                    throw new IllegalStateException("Duplicate leaf setting entry " + id);
                } else {
                    Path path = this.pathProvider.json(id);
                    list.add(DataProvider.saveStable(cache, holder, LeafSetting.CODEC, entry, path));
                }
            };
            this.registerLeafSettings(consumer);

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public @NotNull String getName() {
        return "leaf setting generator";
    }
    
    protected void registerLeafSettings(BiConsumer<Identifier, LeafSetting> consumer) {
        //minecraft
        consumer.accept(LeafLoader.DEFAULT_SETTINGS, new LeafSetting(LeafTypes.DEFAULT));
        consumer.accept(Identifier.fromNamespaceAndPath("minecraft", "spruce_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("minecraft", "jungle_leaves"), new LeafSetting(LeafTypes.DEFAULT));
        consumer.accept(Identifier.fromNamespaceAndPath("minecraft", "cherry_leaves"), new LeafSetting(LeafTypes.CHERRY));
        consumer.accept(Identifier.fromNamespaceAndPath("minecraft", "azalea_leaves"), new LeafSetting(LeafTypes.DEFAULT));
        consumer.accept(Identifier.fromNamespaceAndPath("minecraft", "flowering_azalea_leaves"), new LeafSetting(LeafTypes.AZALEA));
        //byg
        consumer.accept(Identifier.fromNamespaceAndPath("byg", "blue_spruce_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("byg", "cypress_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("byg", "fir_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("byg", "orange_spruce_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("byg", "pine_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("byg", "red_spruce_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("byg", "yellow_spruce_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("byg", "pink_cherry_leaves"), new LeafSetting(LeafTypes.DEFAULT, 1.4));
        consumer.accept(Identifier.fromNamespaceAndPath("byg", "skyris_leaves"), new LeafSetting(LeafTypes.DEFAULT, 1.4));
        consumer.accept(Identifier.fromNamespaceAndPath("byg", "white_cherry_leaves"), new LeafSetting(LeafTypes.DEFAULT, 1.4));
        //terrestria
        consumer.accept(Identifier.fromNamespaceAndPath("terrestria", "cypress_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("terrestria", "hemlock_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("terrestria", "redwood_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("terrestria", "japanese_maple_shrub_leaves"), new LeafSetting(LeafTypes.PALMS, 0));
        consumer.accept(Identifier.fromNamespaceAndPath("terrestria", "jungle_palm_leaves"), new LeafSetting(LeafTypes.PALMS, 0.2));
        consumer.accept(Identifier.fromNamespaceAndPath("terrestria", "yucca_palm_leaves"), new LeafSetting(LeafTypes.PALMS, 0.2));
        consumer.accept(Identifier.fromNamespaceAndPath("terrestria", "sakura_leaves"), new LeafSetting(LeafTypes.DEFAULT, 1.4));
        //traverse
        consumer.accept(Identifier.fromNamespaceAndPath("traverse", "fir_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("traverse", "brown_autumnal_leaves"), new LeafSetting(LeafTypes.DEFAULT, 1.8));
        consumer.accept(Identifier.fromNamespaceAndPath("traverse", "orange_autumnal_leaves"), new LeafSetting(LeafTypes.DEFAULT, 1.8));
        consumer.accept(Identifier.fromNamespaceAndPath("traverse", "red_autumnal_leaves"), new LeafSetting(LeafTypes.DEFAULT, 1.8));
        consumer.accept(Identifier.fromNamespaceAndPath("traverse", "yellow_autumnal_leaves"), new LeafSetting(LeafTypes.DEFAULT, 1.8));
        //woods and mires
        consumer.accept(Identifier.fromNamespaceAndPath("woods_and_mires", "pine_leaves"), new LeafSetting(LeafTypes.CONIFER));
        //biomes o plenty
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "fir_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "redwood_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "cypress_leaves"), new LeafSetting(LeafTypes.CONIFER));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "snowblossom_leaves"), new LeafSetting(LeafTypes.DEFAULT, 0));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "red_maple_leaves"), new LeafSetting(LeafTypes.MAPLE, 0));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "orange_maple_leaves"), new LeafSetting(LeafTypes.MAPLE, 0));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "yellow_maple_leaves"), new LeafSetting(LeafTypes.MAPLE, 0));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "mahogany_leaves"), new LeafSetting(LeafTypes.MAHOGANY, 1));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "pine_leaves"), new LeafSetting(LeafTypes.DEFAULT));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "rainbow_birch_leaves"), new LeafSetting(LeafTypes.DEFAULT));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "jacaranda_leaves"), new LeafSetting(LeafTypes.DEFAULT, 1));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "palm_leaves"), new LeafSetting(LeafTypes.PALMS, 0.2));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "willow_leaves"), new LeafSetting(LeafTypes.DEFAULT));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "dead_leaves"), new LeafSetting(LeafTypes.DEFAULT));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "magic_leaves"), new LeafSetting(LeafTypes.DEFAULT));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "umbran_leaves"), new LeafSetting(LeafTypes.DEFAULT));
        consumer.accept(Identifier.fromNamespaceAndPath("biomesoplenty", "hellbark_leaves"), new LeafSetting(LeafTypes.DEFAULT));
    }
}
