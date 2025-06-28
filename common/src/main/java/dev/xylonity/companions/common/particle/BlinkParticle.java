package dev.xylonity.companions.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class BlinkParticle extends TextureSheetParticle {

    private final SpriteSet spritesset;

    private static final float LARGE_SIZE = 8.0f;
    private static final float SMALL_SIZE = 5.0f;

    BlinkParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z, 0.0, 0.0, 0.0);

        this.rCol = 1F;
        this.gCol = 1F;
        this.bCol = 1F;
        this.lifetime = 5;
        this.setSpriteFromAge(sprites);
        this.spritesset = sprites;
        this.gravity = 0F;
        this.quadSize = LARGE_SIZE;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age < 3) {
            this.quadSize = LARGE_SIZE;
        } else {
            this.quadSize = SMALL_SIZE;
        }

        this.setSpriteFromAge(spritesset);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType particleType, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new BlinkParticle(level, x, y, z, this.sprites);
        }

    }

}