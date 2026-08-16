package de.cheaterpaul.fallingleaves.config;

import de.cheaterpaul.fallingleaves.FallingLeavesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Wind {

    public final ModConfigSpec.BooleanValue enabled;
    private final ModConfigSpec.ConfigValue<List<? extends String>> windlessDimensions;
    private Set<Identifier> windlessDimensionsSet = Set.of();

    public Wind(ModConfigSpec.Builder builder) {
        this.enabled = builder
                .comment("Whether to enable the wind")
                .translation(FallingLeavesMod.MODID + ".configuration.wind.enabled")
                .define("enabled", true);
        this.windlessDimensions = builder
                .comment("The dimension to disable the wind in")
                .translation(FallingLeavesMod.MODID + ".configuration.wind.windlessDimensions")
                .defineListAllowEmpty("windlessDimension", List.of(Level.NETHER.identifier().toString(), Level.END.identifier().toString()), () -> "", Wind::validateDimension);
    }

    public boolean hasWind(Level level) {
        return this.enabled.get() && !this.windlessDimensionsSet.contains(level.dimension().identifier());
    }

    private static boolean validateDimension(Object o) {
        if (o instanceof String dimName) {
            Identifier id = Identifier.tryParse(dimName);
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
        this.windlessDimensionsSet = this.windlessDimensions.get().stream().map(Identifier::parse).collect(Collectors.toSet());
    }
}
