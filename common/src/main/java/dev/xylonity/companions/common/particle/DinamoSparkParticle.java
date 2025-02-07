package dev.xylonity.companions.common.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class DinamoSparkParticle extends TextureSheetParticle {
    private final SpriteSet spritesset;

    public DinamoSparkParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites, double velX, double velY, double velZ) {
        super(world, x, y + 0.5, z, velX, velY, velZ);
        this.spritesset = sprites;
        this.quadSize = 0.3f;
        this.lifetime = RandomSource.create().nextInt(15, 25);
        this.gravity = 0.1F;
        this.friction = 0.95F;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();

        this.setSpriteFromAge(spritesset);

        this.quadSize *= 0.98f;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            double velX = (level.random.nextDouble() - 0.5) * 0.2;
            double velY = 0.3 + level.random.nextDouble() * 0.2;
            double velZ = (level.random.nextDouble() - 0.5) * 0.2;
            return new DinamoSparkParticle(level, x, y, z, this.sprites, velX, velY, velZ);
        }
    }
}
