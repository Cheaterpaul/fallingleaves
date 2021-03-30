package randommcsomethin.fallingleaves.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import randommcsomethin.fallingleaves.config.LeafSettingsEntry;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class LeafSettingGenerator implements IDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final DataGenerator generator;

    public LeafSettingGenerator(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    @Override
    public void run(DirectoryCache cache) throws IOException {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = new HashSet<>();
        this.registerLeafSettingEntries((entry) -> {
            if (!set.add(entry.id)) {
                throw new IllegalStateException("Duplicate leaf setting entry " + entry.id);
            } else {
                JsonObject object = new JsonObject();
                object.addProperty("spawnrate", entry.spawnRateFactor);
                object.addProperty("isConifer", entry.isConiferBlock);
                this.saveLeafSettingEntries(cache, object, path.resolve("data/" + entry.id.getNamespace() + "/fallingleaves/" + entry.id.getPath() + ".json"));
            }
        });
    }

    @Override
    public String getName() {
        return "Falling Leaves leaves settings generator";
    }

    private void saveLeafSettingEntries(DirectoryCache cache, JsonObject entryJson, Path path) {
        try {
            String s = GSON.toJson(entryJson);
            @SuppressWarnings("UnstableApiUsage")
            String s1 = SHA1.hashUnencodedChars(s).toString();
            if (!Objects.equals(cache.getHash(path), s1) || !Files.exists(path)) {
                Files.createDirectories(path.getParent());

                try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
                    bufferedWriter.write(s);
                }
            }
            cache.putNew(path, s1);
        } catch (IOException ioExeption) {
            LOGGER.error("Couldn't save skill node {}", path, ioExeption);
        }
    }

    protected void registerLeafSettingEntries(Consumer<LeafSettingsEntry> consumer) {
        //minecraft
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("minecraft","spruce_leaves"),1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("minecraft","jungle_leaves"),0,false));
        //byg
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","blue_spruce_leaves"),1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","cypress_leaves"),1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","fir_leaves"),1 ,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","orange_spruce_leaves"), 1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","pine_leaves"),1, true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","red_spruce_leaves"),1 ,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","yellow_spruce_leaves"), 1, true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","pink_cherry_leaves"), 1.4, false));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","skyris_leaves"), 1.4, false));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("byg","white_cherry_leaves"), 1.4, false));
        //terrestria
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("terrestria","cypress_leaves"),1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("terrestria","hemlock_leaves"),1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("terrestria","redwood_leaves"),1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("terrestria","japenese_maple_shrub_leaves"),0,false));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("terrestria","jungle_palm_leaves"),0,false));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("terrestria","yucca_palm_leaves"),0,false));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("terrestria","sakura_leaves"),1.4,false));
        //traverse
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("traverse","fir_leaves"),1,true));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("traverse","brown_autumnal_leaves"),1.8,false));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("traverse","orange_autumnal_leaves"),1.8,false));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("traverse","red_autumnal_leaves"),1.8,false));
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("traverse","yellow_autumnal_leaves"),1.8,false));
        //woods and mires
        consumer.accept(new LeafSettingsEntry(new ResourceLocation("woods_and_mires","pine_leaves"),1,true));
    }
}
