
package dev.xylonity.companions.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SoulFlameParticle extends RisingParticle {

    SoulFlameParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.setSpriteFromAge(sprites);
    }

    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void move(double pX, double pY, double pZ) {
        this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
        this.setLocationFromBoundingbox();
    }

    public float getQuadSize(float pScaleFactor) {
        float f = (this.age + pScaleFactor) / this.lifetime;
        return this.quadSize * (1.0F - f * f * 0.5F);
    }

    public int getLightColor(float pPartialTick) {
        float age = (this.age + pPartialTick) / this.lifetime;
        age = Mth.clamp(age, 0.0F, 1.0F);
        int light = super.getLightColor(pPartialTick);
        int light2 = light & 255;
        int light3 = light >> 16 & 255;
        light2 += (int) (age * 15.0F * 16.0F);
        if (light2 > 240) light2 = 240;

        return light2 | light3 << 16;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(@NotNull SimpleParticleType pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new SoulFlameParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprite);
        }
    }

}