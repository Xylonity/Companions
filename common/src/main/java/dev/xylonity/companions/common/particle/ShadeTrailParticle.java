package dev.xylonity.companions.common.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ShadeTrailParticle extends TextureSheetParticle {
    private final SpriteSet spritesset;

    public ShadeTrailParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites, double velX, double velY, double velZ) {
        super(world, x, y + 0.5, z, 0.0, 0.0, 0.0);

        this.quadSize = 0.175f;
        this.rCol = 1F;
        this.gCol = 1F;
        this.bCol = 1F;
        this.lifetime = new Random().nextInt(0, 15) + 20;
        this.setSpriteFromAge(sprites);
        this.spritesset = sprites;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
        super.render(pBuffer, pRenderInfo, pPartialTicks);
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

        public Particle createParticle(@NotNull SimpleParticleType particleType, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new ShadeTrailParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }

}