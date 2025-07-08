package dev.xylonity.companions.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CakeCreamParticle extends TextureSheetParticle {
    private final SpriteSet spritesset;
    private boolean hasLanded = false;
    private int landedTicks = 0;

    private static double defaultVelocityX = 0;
    private static double defaultVelocityY = 0;
    private static double defaultVelocityZ = 0;

    public static void setDefaultVelocity(double vx, double vy, double vz) {
        defaultVelocityX = vx;
        defaultVelocityY = vy;
        defaultVelocityZ = vz;
    }

    CakeCreamParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites, double velX, double velY, double velZ) {
        super(world, x, y + 0.5, z);
        this.quadSize = new Random().nextFloat(0.15f, 0.40f);
        this.lifetime = new Random().nextInt(50) + 20;
        this.setSpriteFromAge(sprites);
        this.spritesset = sprites;
        this.gravity = 0.15f;
        this.alpha = 0;

        if (velX == 0 && velY == 0 && velZ == 0) {
            this.xd = defaultVelocityX;
            this.yd = defaultVelocityY;
            this.zd = defaultVelocityZ;
        } else {
            this.xd = velX;
            this.yd = velY;
            this.zd = velZ;
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        if (hasLanded) {
            landedTicks++;
            if (landedTicks >= 40) {
                this.remove();
            }
            return;
        }

        super.tick();
        this.setSpriteFromAge(spritesset);

        if (this.age < 4) {
            this.alpha = (float)this.age / 5.0F;
        } else {
            this.alpha = 1.0F;
        }

        this.xd *= 0.98;
        this.yd *= 0.98;
        this.zd *= 0.98;
    }

    public static Vec3 randomVectorInCone(Vec3 base, double alpha) {
        Vec3 baseNorm = base.normalize();

        Vec3 u = baseNorm.cross(new Vec3(0, 1, 0));
        if (u.lengthSqr() < 1e-6) {
            u = baseNorm.cross(new Vec3(1, 0, 0));
        }

        u = u.normalize();
        Vec3 v = baseNorm.cross(u).normalize();

        double minCos = Math.cos(Math.toRadians(alpha));
        double cos = minCos + new Random().nextDouble() * (1.0 - minCos);
        double sin = Math.sqrt(1.0 - cos * cos);
        double phi = new Random().nextDouble() * 2 * Math.PI;
        return baseNorm.scale(cos).add(u.scale(sin * Math.cos(phi))).add(v.scale(sin * Math.sin(phi))).scale(base.length());
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            if (dx == 0 && dy == 0 && dz == 0) {
                Vec3 b = new Vec3(defaultVelocityX, defaultVelocityY, defaultVelocityZ);
                Vec3 v = randomVectorInCone(b, 15.0);
                double f = 0.8 + new Random().nextDouble() * 0.4;
                dx = v.x * f;
                dy = v.y * f;
                dz = v.z * f;
            }

            CakeCreamParticle particle = new CakeCreamParticle(level, x, y, z, this.sprites, dx, dy, dz);
            particle.setPos(x, y, z);
            return particle;
        }

    }
}