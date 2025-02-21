
package dev.xylonity.companions.common.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.joml.Quaternionf;

import java.util.Random;

public class BlackHoleStarParticle extends TextureSheetParticle {
    private final SpriteSet spritesset;
    private double angle;
    private double radius;

    BlackHoleStarParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites, double velX, double velY, double velZ) {
        super(world, x, y + 0.5, z, 0.0, 0.0, 0.0);

        this.quadSize = 0.15f;
        this.lifetime = new Random().nextInt(0, 15) + 20;
        this.setSpriteFromAge(sprites);
        this.spritesset = sprites;

        this.angle = random.nextDouble() * 2 * Math.PI;
        this.radius = random.nextDouble() * 0.5;
        this.y += random.nextDouble() - 0.5;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(spritesset);

        angle += 0.30;
        radius += 0.0008;

        this.x = xo + radius * Math.cos(angle) + (random.nextDouble() - 0.5) * 0.1;
        this.z = zo + radius * Math.sin(angle) + (random.nextDouble() - 0.5) * 0.1;

        this.y = yo;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new BlackHoleStarParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }

}