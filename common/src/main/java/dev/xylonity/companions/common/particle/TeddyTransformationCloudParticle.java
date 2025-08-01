package dev.xylonity.companions.common.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

public class TeddyTransformationCloudParticle extends TextureSheetParticle {
    private final SpriteSet spritesset;
    private static final Quaternionf QUATERNION = new Quaternionf(0F, +0.7F, -0.7F, 0F);

    protected TeddyTransformationCloudParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites, double velX, double velY, double velZ) {
        super(world, x, y, z, 0.0, 0.0, 0.0);

        this.quadSize = (float) 1.5;
        this.rCol = 1F;
        this.gCol = 1F;
        this.bCol = 1F;
        this.lifetime = 20;
        this.setSpriteFromAge(sprites);
        this.spritesset = sprites;
        this.setParticleSpeed(0, 0, 0);

        //this.alpha = 0.0F;

        //float darknessFactor = 0.75F + (this.random.nextFloat() * 0.25F);
        //this.setColor(darknessFactor * this.rCol, darknessFactor * this.gCol, darknessFactor * this.bCol);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(@NotNull VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        Vec3 vec3 = pRenderInfo.getPosition();
        float x = (float) (Mth.lerp(pPartialTicks, this.xo, this.x) - vec3.x());
        float y = (float) (Mth.lerp(pPartialTicks, this.yo, this.y) - vec3.y());
        float z = (float) (Mth.lerp(pPartialTicks, this.zo, this.z) - vec3.z());

        Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        Vector3f[] vector3fsBottom = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, -1.0F, 0.0F)};

        float f4 = this.getQuadSize(pPartialTicks);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = vector3fs[i];
            vector3f.rotate(QUATERNION);
            vector3f.mul(f4);
            vector3f.add(x, y, z);

            Vector3f vector3fBottom = vector3fsBottom[i];
            vector3fBottom.rotate(QUATERNION);
            vector3fBottom.mul(f4);
            vector3fBottom.add(x, y - 0.1F, z);
        }

        float f7 = this.getU0();
        float f8 = this.getU1();
        float f5 = this.getV0();
        float f6 = this.getV1();
        int light = this.getLightColor(pPartialTicks);

        pBuffer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        pBuffer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        pBuffer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        pBuffer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();

        pBuffer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        pBuffer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        pBuffer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        pBuffer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
    }

    @Override
    public void tick() {
        super.tick();
        //int fadeInEnd = 20;
        //int fadeOutStart = this.lifetime - 20;
//
        //if (this.age < fadeInEnd) {
        //    this.alpha = (float)this.age / (float)fadeInEnd;
        //} else if (this.age >= fadeOutStart) {
        //    float fadeProgress = (float)(this.age - fadeOutStart) / 20.0F;
        //    this.alpha = 1.0F - fadeProgress;
        //} else {
        //    this.alpha = 1.0F;
        //}

        if (this.age >= this.lifetime) {
            this.remove();
        }

        this.setSpriteFromAge(spritesset);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType particleType, @NotNull ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new TeddyTransformationCloudParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}