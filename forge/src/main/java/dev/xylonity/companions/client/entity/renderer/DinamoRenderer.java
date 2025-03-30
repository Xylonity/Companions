package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.blockentity.renderer.TeslaReceiverRenderer;
import dev.xylonity.companions.client.entity.model.DinamoModel;
import dev.xylonity.companions.common.entity.ai.illagergolem.TeslaConnectionManager;
import dev.xylonity.companions.common.entity.custom.DinamoEntity;
import dev.xylonity.companions.common.event.ClientEntityTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class DinamoRenderer extends GeoEntityRenderer<DinamoEntity> {

    public DinamoRenderer(EntityRendererProvider.Context renderManager, int totalFrames, int ticksPerFrame) {
        super(renderManager, new DinamoModel());
        addRenderLayer(new ElectricConnectionLayer(this,
                new ResourceLocation(Companions.MOD_ID, "textures/misc/electric_arch.png"),
                totalFrames,
                ticksPerFrame
        ));
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    //public static RenderType decacheShaderRendering(ResourceLocation texture) {
    //    return RenderType.create("dynamic_electric_arch", DefaultVertexFormat.NEW_ENTITY,
    //            VertexFormat.Mode.QUADS, 256, false, true,
    //            RenderType.CompositeState.builder()
    //                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityCutoutShader))
    //                    .setTextureState(new RenderStateShard.ShaderStateShard.TextureStateShard(texture, false, false))
    //                    .setCullState(new RenderStateShard.CullStateShard(false))
    //                    .setLightmapState(new RenderStateShard.LightmapStateShard(true))
    //                    .setOverlayState(new RenderStateShard.OverlayStateShard(true))
    //                    .createCompositeState(false));
    //}

    public DinamoRenderer(EntityRendererProvider.Context renderManager) {
        this(renderManager, 8, DinamoEntity.ELECTRICAL_CHARGE_DURATION / 8);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DinamoEntity animatable) {
        TeslaConnectionManager connectionManager = TeslaConnectionManager.getInstance();
        TeslaConnectionManager.ConnectionNode node = animatable.asConnectionNode();

        if ((!connectionManager.getIncoming(node).isEmpty() || !connectionManager.getOutgoing(node).isEmpty()) && animatable.isActive()) {
            return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/dinamo_charge.png");
        }

        return new ResourceLocation(Companions.MOD_ID, "textures/entity/dinamo.png");
    }

    private static class ElectricConnectionLayer extends GeoRenderLayer<DinamoEntity> {
        private final ResourceLocation texture;
        private final int totalFrames;
        private final int ticksPerFrame;

        public ElectricConnectionLayer(GeoRenderer<DinamoEntity> renderer, ResourceLocation texture, int totalFrames, int ticksPerFrame) {
            super(renderer);
            this.texture = texture;
            this.totalFrames = totalFrames;
            this.ticksPerFrame = ticksPerFrame;
        }

        @Override
        public void render(PoseStack poseStack, DinamoEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

            int frame = calculateCurrentFrame(animatable);

            if (frame < 0) return;

            if (animatable.isSitting()) {
                if (!animatable.isActive()) return;

                for (TeslaConnectionManager.ConnectionNode e : TeslaConnectionManager.getInstance().getOutgoing(animatable.asConnectionNode())) {
                    if (e.isEntity()) {
                        Entity entity = ClientEntityTracker.getEntityByUUID(e.entityId());
                        if (entity instanceof LivingEntity livingEntity) {
                            Vec3 offset = new Vec3(0.0D, animatable.getBbHeight() * 0.90D, 0.0D);
                            Vec3 direction = livingEntity.position()
                                    .subtract(animatable.position())
                                    .add(0.0D, livingEntity.getBbHeight() * 0.90D, 0.0D);

                            renderConnection(bufferSource, poseStack, offset, direction, frame, packedLight);
                        }
                    } else if (e.isBlock()) {
                        Vec3 offset = new Vec3(0.0D, animatable.getBbHeight() * 0.90D, 0.0D);
                        BlockPos blockPos = e.blockPos();

                        Vec3 blockPosVec = new Vec3(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D);
                        Vec3 animatablePos = animatable.position();
                        Vec3 direction = blockPosVec.subtract(animatablePos).add(0.0D, 1.25D, 0.0D);

                        renderConnection(bufferSource, poseStack, offset, direction, frame, packedLight);
                    }
                }

            } else {
                if (!animatable.isActiveForAttack() && !animatable.shouldAttack()) return;

                if (!animatable.isSitting()) {
                    for (Entity e : animatable.visibleEntities) {
                        Vec3 offset = new Vec3(0.0D, animatable.getBbHeight() * 0.95D, 0.0D);
                        Vec3 direction = e.position().subtract(animatable.position()).add(0.0D, e.getBbHeight() * 0.5D, 0.0D);
                        renderConnection(bufferSource, poseStack, offset, direction, frame, packedLight);
                    }
                }

            }

        }

        private int calculateCurrentFrame(DinamoEntity animatable) {
            int elapsedTicks = animatable.getTickCount() - animatable.getAnimationStartTick();
            int frame = elapsedTicks / ticksPerFrame;

            if (frame >= totalFrames) return -1;

            return frame;
        }

        // Original idea of the layer embedded in the entity renderer to force the rendering of a singular "electric arch" towards a certain target by mim1q
        // https://github.com/mim1q/MineCells/blob/1.20.x/src/main/java/com/github/mim1q/minecells/client/render/ProtectorEntityRenderer.java
        private void renderConnection(MultiBufferSource bufferSource, PoseStack poseStack, Vec3 p0, Vec3 p1, int frame, int light) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(AutoGlowingTexture.getRenderType(texture));
            //VertexConsumer vertexConsumer = bufferSource.getBuffer(decacheShaderRendering(texture));
            //VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(texture));
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

}
