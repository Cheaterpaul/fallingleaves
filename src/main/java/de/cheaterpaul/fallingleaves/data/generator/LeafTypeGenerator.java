package de.cheaterpaul.fallingleaves.data.generator;

import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafType;
import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafTypes;
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

import static de.cheaterpaul.fallingleaves.FallingLeavesMod.modId;

public class LeafTypeGenerator implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> holderLookup;

    public LeafTypeGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> holderLookup) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "fallingleaves/leaftypes");
        this.holderLookup = holderLookup;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        return this.holderLookup.thenCompose((holder) -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            BiConsumer<ResourceLocation, LeafType> consumer = (id, entry) -> {
                if (!set.add(id)){
                    throw new IllegalStateException("Duplicate leaf type entry " + id);
                } else {
                    Path path = this.pathProvider.json(id);
                    list.add(DataProvider.saveStable(cache, holder, LeafType.CODEC, entry, path));
                }
            };
            this.registerLeafTypes(consumer);

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public @NotNull String getName() {
        return "leave type generator";
    }

    protected void registerLeafTypes(BiConsumer<ResourceLocation, LeafType> consumer) {
        consumer.accept(LeafTypes.DEFAULT, new LeafType(modId("default_1"), modId("default_2"), modId("default_3"), modId("default_4"), modId("default_5")));
        consumer.accept(LeafTypes.CONIFER, new LeafType(0.2f,1,1.5f, modId("conifer_1"), modId("conifer_2"), modId("conifer_3")));
        consumer.accept(LeafTypes.PALMS, new LeafType(0.5f,4,2, modId("palms_1"), modId("palms_2")));
        consumer.accept(LeafTypes.MAHOGANY, new LeafType(modId("mahogany_1"), modId("mahogany_2")));
        consumer.accept(LeafTypes.MAPLE, new LeafType(modId("maple_1"), modId("maple_1")));
        consumer.accept(LeafTypes.CHERRY, new LeafType(List.of(new LeafType.Texture(modId("cherry_flower_1"), true, 2), new LeafType.Texture(modId("cherry_flower_1"), true, 2), new LeafType.Texture(modId("cherry_1"), true)), 1.3f,1f,1f));
        consumer.accept(LeafTypes.AZALEA, new LeafType(List.of(new LeafType.Texture(modId("azalea_flower_1"), true, 2)), 1.3f,1f,1f));

    }
}
