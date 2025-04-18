package dev.xylonity.companions.common.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class FireworkToadParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private final int mode;

    FireworkToadParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites, double velX, double velY, double velZ) {
        super(world, x, y + 0.5, z, velX, velY, velZ);

        this.spriteSet = sprites;
        this.quadSize = 0.15f;
        this.lifetime = new Random().nextInt(15) + 20;
        this.gravity = 0.1f;
        this.hasPhysics = true;
        this.friction = 0.98f;
        this.mode = new Random().nextInt(3);
        this.setSpriteFromAge(sprites);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(spriteSet);

        switch (mode) {
            case 0 -> this.setAlpha((this.age % 4 < 2) ? 1.0f : 0.0f);
            case 2 -> {
                this.setAlpha(0);
                if (this.age % 2 == 0) {
                    this.level.addParticle(ParticleTypes.END_ROD, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                }
            }
            default -> {}
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            float speed = 0.5F;
            int size = 1;

            ParticleEngine engine = Minecraft.getInstance().particleEngine;
            Random r = new Random();

            // Captured from the vanilla's large firework
            for (int i = -size; i <= size; ++i) {
                for (int j = -size; j <= size; ++j) {
                    for (int k = -size; k <= size; ++k) {
                        double rx = j + (r.nextDouble() - r.nextDouble()) * 0.5;
                        double ry = i + (r.nextDouble() - r.nextDouble()) * 0.5;
                        double rz = k + (r.nextDouble() - r.nextDouble()) * 0.5;
                        double norm = Math.sqrt(rx*rx + ry*ry + rz*rz) / speed + r.nextGaussian() * 0.05;
                        rx /= norm; ry /= norm; rz /= norm;

                        FireworkToadParticle p = new FireworkToadParticle(level, x, y, z, this.sprites, rx, ry, rz);

                        p.setAlpha(0.99F);

                        engine.add(p);
                    }
                }
            }

            return null;
        }
    }

}
