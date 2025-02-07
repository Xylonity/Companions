package dev.xylonity.companions.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class IllagerGolemSparkParticle extends TextureSheetParticle {

    private final SpriteSet spritesset;

    protected IllagerGolemSparkParticle(ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(clientLevel, x, y, z, dx, dy, dz);

        this.spritesset = sprites;
        this.quadSize = 0.15F;
        this.lifetime = 20;
        this.gravity = 1F;
        this.friction = 0.8F;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(spritesset);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            RandomSource random = level.random;
            double motionX = (random.nextDouble() - 0.5) * 0.15;
            double motionZ = (random.nextDouble() - 0.5) * 0.15;

            return new IllagerGolemSparkParticle(level, x, y, z, motionX, -0.1, motionZ, this.sprites);
        }
    }
}