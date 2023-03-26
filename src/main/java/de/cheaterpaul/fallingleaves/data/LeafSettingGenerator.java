package de.cheaterpaul.fallingleaves.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.cheaterpaul.fallingleaves.config.LeafSettingsEntry;
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
import java.util.function.Consumer;

public class LeafSettingGenerator implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput.PathProvider pathProvider;

    public LeafSettingGenerator(PackOutput packOutput) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "fallingleaves/settings");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        return CompletableFuture.supplyAsync(() -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            Consumer<LeafSettingsEntry> consumer = entry -> {
                if (!set.add(entry.id())){
                    throw new IllegalStateException("Duplicate leaf setting entry " + entry.id());
                } else {
                    Path path = this.pathProvider.json(entry.id());
                    list.add(DataProvider.saveStable(cache, entry.serializeToJson(), path));
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

    protected void registerLeafSettingEntries(Consumer<LeafSettingsEntry> consumer) {
        //minecraft
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("minecraft","spruce_leaves"),1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("minecraft","jungle_leaves"),0));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("minecraft","cherry_leaves"),0));
        //byg
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","blue_spruce_leaves"),1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","cypress_leaves"),1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","fir_leaves"),1 ,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","orange_spruce_leaves"), 1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","pine_leaves"),1, true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","red_spruce_leaves"),1 ,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","yellow_spruce_leaves"), 1, true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","pink_cherry_leaves"), 1.4));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","skyris_leaves"), 1.4));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","white_cherry_leaves"), 1.4));
        //terrestria
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("terrestria","cypress_leaves"),1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("terrestria","hemlock_leaves"),1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("terrestria","redwood_leaves"),1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("terrestria","japenese_maple_shrub_leaves"),0));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("terrestria","jungle_palm_leaves"),0));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("terrestria","yucca_palm_leaves"),0));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("terrestria","sakura_leaves"),1.4));
        //traverse
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("traverse","fir_leaves"),1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("traverse","brown_autumnal_leaves"),1.8));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("traverse","orange_autumnal_leaves"),1.8));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("traverse","red_autumnal_leaves"),1.8));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("traverse","yellow_autumnal_leaves"),1.8));
        //woods and mires
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("woods_and_mires","pine_leaves"),1,true));
    }
}
