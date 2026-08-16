package de.cheaterpaul.fallingleaves.data.generator;

import de.cheaterpaul.fallingleaves.leaves.mod.types.LeafType;
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
            Set<Identifier> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            BiConsumer<Identifier, LeafType> consumer = (id, entry) -> {
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

    protected void registerLeafTypes(BiConsumer<Identifier, LeafType> consumer) {
        consumer.accept(LeafTypes.DEFAULT, new LeafType(1f,1f,1f,
                modId("default_1"), modId("default_2"), modId("default_3"), modId("default_4"), modId("default_5")));
        consumer.accept(LeafTypes.CONIFER, new LeafType(0.2f,1,1.5f,
                new LeafType.SeasonModifier(new LeafType.Season(1), new LeafType.Season(1), new LeafType.Season(1), new LeafType.Season(1)),
                modId("conifer_1"), modId("conifer_2"), modId("conifer_3")));
        consumer.accept(LeafTypes.PALMS, new LeafType(0.5f,4f,2f,
                modId("palms_1"), modId("palms_2")));
        consumer.accept(LeafTypes.MAHOGANY, new LeafType(1f,1f,1f,
                modId("mahogany_1"), modId("mahogany_2")));
        consumer.accept(LeafTypes.MAPLE, new LeafType(1f,1f,1f,
                modId("maple_1"), modId("maple_1")));
        consumer.accept(LeafTypes.CHERRY, new LeafType( 1.3f,1f,1f,
                new LeafType.SeasonModifier(new LeafType.Season(0.2f, 0.5f, 1.2f), new LeafType.Season(0.8f, 0.4f, 0), new LeafType.Season(0), new LeafType.Season(0)),
                new LeafType.Texture(modId("cherry_flower_1"), true, 2), new LeafType.Texture(modId("cherry_flower_1"), true, 2), new LeafType.Texture(modId("cherry_1"), true)));
        consumer.accept(LeafTypes.AZALEA, new LeafType(1.3f,1f,1f,
                new LeafType.SeasonModifier(new LeafType.Season(0.2f, 0.5f, 1.2f), new LeafType.Season(0.8f, 0.4f, 0), new LeafType.Season(0), new LeafType.Season(0)),
                new LeafType.Texture(modId("azalea_flower_1"), true, 2)));

    }
}
