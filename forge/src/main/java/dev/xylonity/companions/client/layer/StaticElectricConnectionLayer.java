package dev.xylonity.companions.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class StaticElectricConnectionLayer<T extends AbstractTeslaBlockEntity> extends GeoRenderLayer<T> {
    private final ResourceLocation texture;
    private final int totalFrames;
    private final int ticksPerFrame;

    public StaticElectricConnectionLayer(GeoRenderer<T> renderer, ResourceLocation texture, int totalFrames, int ticksPerFrame) {
        super(renderer);
        this.texture = texture;
        this.totalFrames = totalFrames;
        this.ticksPerFrame = ticksPerFrame;
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (!animatable.isActive()) return;

        int frame = calculateCurrentFrame(animatable);

        for (TeslaConnectionManager.ConnectionNode node : TeslaConnectionManager.getInstance().getOutgoing(animatable.asConnectionNode())) {
            if (node != null) {
                if (node.isEntity()) {
                    Entity entity = CompanionsEntityTracker.getEntityByUUID(node.entityId());
                    if (entity instanceof LivingEntity livingEntity) {
                        Vec3 offset = new Vec3(0.0D, 1.25D, 0.0D);
                        Vec3 dir = livingEntity.position().subtract(animatable.getBlockPos().getCenter()).add(0.0D, livingEntity.getBbHeight() * 1.1D, 0.0D);

                        renderConnection(bufferSource, poseStack, offset, dir, frame, packedLight);
                    }
                } else if (node.isBlock()) {
                    AbstractTeslaBlockEntity be = TeslaConnectionManager.getInstance().getBlockEntity(node);
                    if (be instanceof VoltaicPillarBlockEntity) {
                        Vec3 offset = animatable.electricalChargeOriginOffset();
                        Vec3 blockPos = be.getBlockPos().getCenter();

                        Vec3 blockPosVec = new Vec3(blockPos.x, blockPos.y, blockPos.z);
                        Vec3 dir = blockPosVec.subtract(animatable.getBlockPos().getCenter()).add(be.electricalChargeEndOffset());

                        renderConnection(bufferSource, poseStack, offset, dir, frame, packedLight);
                    }
                }
            }

        }

    }

    private int calculateCurrentFrame(T animatable) {
        if (animatable.getLevel() != null) {
            long elapsed = animatable.getLevel().getGameTime() - animatable.getAnimationStartTick();
            return (int) (elapsed / ticksPerFrame) % totalFrames + 1;
        }

        return 0;
    }

    /**
     * Code of the 3D plate rendering adapted from mim1q's work
     * https://github.com/mim1q/MineCells/blob/1.20.x/src/main/java/com/github/mim1q/minecells/client/render/ProtectorEntityRenderer.java
     */
    private void renderConnection(MultiBufferSource bufferSource, PoseStack poseStack, Vec3 p0, Vec3 p1, int frame, int light) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(AutoGlowingTexture.getRenderType(texture));
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
            produceVertex(vertexConsumer, positionMatrix, normalMatrix, light, vertices[i].x, vertices[i].y, vertices[i].z, vertices[i].u, vertices[i].v);
        }
    }

    private record VertexCoordinates(float x, float y, float z, float u, float v) { ;; }

    private void produceVertex(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, int light, float x, float y, float z, float textureU, float textureV) {
        vertexConsumer.vertex(positionMatrix, x, y, z)
                .color(255, 255, 255, 255)
                .uv(textureU, textureV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

}