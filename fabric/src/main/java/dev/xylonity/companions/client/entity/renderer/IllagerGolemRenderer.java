package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.entity.model.IllagerGolemModel;
import dev.xylonity.companions.common.entity.hostile.IllagerGolemEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class IllagerGolemRenderer extends GeoEntityRenderer<IllagerGolemEntity> {

    public IllagerGolemRenderer(EntityRendererProvider.Context renderManager, int totalFrames, int ticksPerFrame) {
        super(renderManager, new IllagerGolemModel());
        addRenderLayer(new ElectricConnectionLayer(this,
                ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/misc/illager_golem_electric_arch.png"),
                totalFrames,
                ticksPerFrame
        ));
        this.shadowRadius = 1f;
    }

    public IllagerGolemRenderer(EntityRendererProvider.Context renderManager) {
        this(renderManager, IllagerGolemEntity.ELECTRICAL_CHARGE_DURATION, 1);
    }

    private static class ElectricConnectionLayer extends GeoRenderLayer<IllagerGolemEntity> {
        private final ResourceLocation texture;
        private final int totalFrames;
        private final int ticksPerFrame;

        public ElectricConnectionLayer(GeoRenderer<IllagerGolemEntity> renderer, ResourceLocation texture, int totalFrames, int ticksPerFrame) {
            super(renderer);
            this.texture = texture;
            this.totalFrames = totalFrames;
            this.ticksPerFrame = ticksPerFrame;
        }

        @Override
        public void render(PoseStack poseStack, IllagerGolemEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

            if (!animatable.isActive()) return;

            int frame = calculateCurrentFrame(animatable);

            if (frame < 0) return;

            for (Entity e : animatable.visibleEntities) {
                Vec3 offset = new Vec3(0.0D, animatable.getBbHeight() * 0.95D, 0.0D);
                Vec3 direction = e.position().subtract(animatable.position()).add(0.0D, e.getBbHeight() * 0.5D, 0.0D);
                renderConnection(bufferSource, poseStack, offset, direction, frame, packedLight);
            }

        }

        private int calculateCurrentFrame(IllagerGolemEntity animatable) {
            int elapsedTicks = animatable.getTickCount() - animatable.getAnimationStartTick();
            int frame = elapsedTicks / ticksPerFrame;

            if (frame >= totalFrames) return -1;

            return frame;
        }

        private void renderConnection(MultiBufferSource bufferSource, PoseStack poseStack, Vec3 p0, Vec3 p1, int frame, int light) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(texture));
            Matrix4f positionMatrix = poseStack.last().pose();
            Matrix3f normalMatrix = poseStack.last().normal();

            float x0 = (float) p0.x;
            float y0 = (float) p0.y;
            float z0 = (float) p0.z;
            float x1 = (float) p1.x;
            float y1 = (float) p1.y;
            float z1 = (float) p1.z;

            float dx = x1 - x0;
            float dy = y1 - y0;
            float dz = z1 - z0;

            if (dx == 0.0F) {
                dx = 0.001F;
            }

            float dHorizontal = Mth.sqrt(dx * dx + dz * dz);
            float length = Mth.sqrt(dHorizontal * dHorizontal + dy * dy);

            float offset = 0.5F;
            float yOffset = offset * (dHorizontal / length);
            float xOffset = offset * (dy / length) * (dx / dHorizontal);
            float zOffset = offset * (dy / length) * (dz / dHorizontal);

            float frameSize = 1.0F / totalFrames;
            float v0 = frame * frameSize;
            float v1 = v0 + frameSize;

            VertexCoordinates[] vertices = {
                    new VertexCoordinates(x0 + xOffset, y0 - yOffset, z0 + zOffset, 0.0F, v1),
                    new VertexCoordinates(x1 + xOffset, y1 - yOffset, z1 + zOffset, 1.0F, v1),
                    new VertexCoordinates(x1 - xOffset, y1 + yOffset, z1 - zOffset, 1.0F, v0),
                    new VertexCoordinates(x0 - xOffset, y0 + yOffset, z0 - zOffset, 0.0F, v0),
            };

            int[] indices = {0, 1, 2, 3, 3, 2, 1, 0};
            for (int i : indices) {
                produceVertex(vertexConsumer, positionMatrix, poseStack, light, vertices[i].x, vertices[i].y, vertices[i].z, vertices[i].u, vertices[i].v);
            }
        }

        private record VertexCoordinates(float x, float y, float z, float u, float v) { ;; }

        private void produceVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, PoseStack poseStack, int light, float x, float y, float z, float textureU, float textureV) {
            vertexConsumer.addVertex(positionMatrix, x, y, z)
                    .setColor(255, 255, 255, 255)
                    .setUv(textureU, textureV)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F);
        }

    }

}
