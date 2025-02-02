package dev.xylonity.companions.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class IllagerGolemFlashParticle extends TextureSheetParticle {

    protected final float maxSize;

    protected IllagerGolemFlashParticle(ClientLevel clientLevel, double x, double y, double z) {
        super(clientLevel, x, y, z);
        //this.setParticleSpeed(0.0D, 0.0D, 0.0D);
        this.lifetime = clientLevel.random.nextInt(20) + 10;
        this.roll = clientLevel.random.nextFloat() * Mth.PI * 2.0F;
        this.oRoll = this.roll;
        this.maxSize = this.lifetime * 0.05F;
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 1.0F - ((float) this.age / (float) this.lifetime);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return this.maxSize * (((float) this.age + scaleFactor) / (float) this.lifetime);
    }

    @Override
    public void setSpriteFromAge(SpriteSet spriteSet) {
        this.setSprite(spriteSet.get(this.age, this.lifetime));
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            IllagerGolemFlashParticle particle = new IllagerGolemFlashParticle(level, x, y, z);
            particle.setSpriteFromAge(this.spriteSet);
            particle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
            return particle;
        }
    }
}
