package de.cheaterpaul.fallingleaves.config;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Config {

    public final Wind wind;

    public Config(ModConfigSpec.Builder builder) {
        wind = new Wind(builder);
    }

    public void onLoad(final ModConfigEvent event) {
        wind.onLoad(event);
    }

    public static class Wind {

        public final ModConfigSpec.BooleanValue enabled;
        private final ModConfigSpec.ConfigValue<List< ? extends String>> windlessDimensions;
        private Set<ResourceLocation> windlessDimensionsSet = Set.of();

        public Wind(ModConfigSpec.Builder builder) {
            this.enabled = builder.comment("Whether to enable the wind").define("enabled", true);
            this.windlessDimensions = builder.comment("The dimension to disable the wind in").defineListAllowEmpty("windlessDimension", List.of(Level.NETHER.location().toString(), Level.END.location().toString()), () -> "", Wind::validateDimension);
        }

        public boolean hasWind(Level level) {
            return this.enabled.get() && !this.windlessDimensionsSet.contains(level.dimension().location());
        }

        private static boolean validateDimension(Object o) {
            if(o instanceof String dimName) {
                ResourceLocation id = ResourceLocation.tryParse(dimName);
                if (id != null) {
                    if (ServerLifecycleHooks.getCurrentServer() != null) {
                        return ServerLifecycleHooks.getCurrentServer().registryAccess().lookupOrThrow(Registries.DIMENSION).containsKey(id);
                    } else {
                        return true;
                    }
                }
            }
            return false;
        }

        public void onLoad(final ModConfigEvent event) {
            this.windlessDimensionsSet = this.windlessDimensions.get().stream().map(ResourceLocation::parse).collect(Collectors.toSet());
        }
    }
}
