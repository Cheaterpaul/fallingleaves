package de.cheaterpaul.fallingleaves.particle;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ColoredSpriteProvider {

    List<TextureSprite> sprites;

    public ColoredSpriteProvider(List<TextureSprite> sprites) {
        this.sprites = List.copyOf(sprites);
    }

    public @NotNull TextureSprite get(RandomSource randomSource) {
        return this.sprites.get(randomSource.nextInt(this.sprites.size()));
    }

    public record TextureSprite(TextureAtlasSprite sprite, boolean isTinted, float sizeModifier) {

    }
}
