package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.entity.model.DinamoModel;
import dev.xylonity.companions.client.layer.ElectricArcRenderer;
import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.entity.companion.DinamoEntity;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.common.tesla.ConnectionTarget;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class DinamoRenderer extends GeoEntityRenderer<DinamoEntity> implements ITeslaUtil {

    public DinamoRenderer(EntityRendererProvider.Context renderManager, int totalFrames, int ticksPerFrame) {
        super(renderManager, new DinamoModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        addRenderLayer(new DinamoArcLayer(this, Companions.of("textures/misc/electric_arch.png"), totalFrames, ticksPerFrame));
        this.shadowRadius = 1f;
    }

    public DinamoRenderer(EntityRendererProvider.Context renderManager) {
        this(renderManager, 8, ELECTRICAL_CHARGE_DURATION / 8);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DinamoEntity entity) {
        return getGeoModel().getTextureResource(animatable, this);
    }

    private static class DinamoArcLayer extends GeoRenderLayer<DinamoEntity> {

        private final ResourceLocation texture;
        private final int totalFrames;
        private final int ticksPerFrame;

        DinamoArcLayer(GeoRenderer<DinamoEntity> renderer, ResourceLocation texture, int totalFrames, int ticksPerFrame) {
            super(renderer);
            this.texture = texture;
            this.totalFrames = totalFrames;
            this.ticksPerFrame = ticksPerFrame;
        }

        @Override
        public void render(PoseStack poseStack, DinamoEntity dinamo, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

            final int frame = calculateFrame(dinamo);
            if (frame < 0) {
                return;
            }

            if (dinamo.getMainAction() == 0 && dinamo.isActive()) {
                renderPulseConnections(poseStack, bufferSource, dinamo, frame, packedLight);
            }
            else if (dinamo.getMainAction() != 0 && dinamo.isActiveForAttack()) {
                renderAttackConnections(poseStack, bufferSource, dinamo, frame, packedLight);
            }

        }

        private void renderPulseConnections(PoseStack poseStack, MultiBufferSource bufferSource, DinamoEntity dinamo, int frame, int light) {
            final Vec3 origin = dinamo.position();
            final Vec3 originOffset = new Vec3(0, dinamo.getBbHeight() * 0.8, 0);

            for (final ConnectionTarget target : dinamo.getOutgoing()) {
                Vec3 direction;
                if (target.isEntity()) {
                    final Entity entity = CompanionsEntityTracker.getEntityByUUID(target.entityId());
                    if (!(entity instanceof LivingEntity livingEntity)) {
                        continue;
                    }

                    direction = livingEntity.position().add(0, dinamo.getBbHeight() * 0.8, 0).subtract(origin);
                }
                else if (target.isBlock()) {
                    final BlockEntity blockEntity = dinamo.level().getBlockEntity(target.blockPos());
                    if (!(blockEntity instanceof AbstractTeslaBlockEntity teslaBlockEntity)) {
                        continue;
                    }

                    final Vec3 base = new Vec3(teslaBlockEntity.getBlockPos().getX() + 0.5, teslaBlockEntity.getBlockPos().getY(), teslaBlockEntity.getBlockPos().getZ() + 0.5);
                    direction = base.add(teslaBlockEntity.electricalChargeEndOffset()).subtract(origin);
                }
                else {
                    continue;
                }

                ElectricArcRenderer.renderArc(bufferSource, poseStack, originOffset, direction, frame, totalFrames, texture, false, light);
            }

        }

        private void renderAttackConnections(PoseStack poseStack, MultiBufferSource bufferSource, DinamoEntity dinamo, int frame, int light) {
            final Vec3 origin = dinamo.position();
            final Vec3 originOffset = new Vec3(0, dinamo.getBbHeight() * 0.8, 0);

            for (final LivingEntity target : dinamo.entitiesToAttack) {
                final Vec3 end = target.position().add(0, target.getBbHeight() * 0.5, 0);
                final Vec3 direction = end.subtract(origin);

                ElectricArcRenderer.renderArc(bufferSource, poseStack, originOffset, direction, frame, totalFrames, texture, false, light);
            }

        }

        private int calculateFrame(DinamoEntity dinamo) {
            int f = dinamo.getAnimationStartTick() / ticksPerFrame;
            return (f >= totalFrames) ? -1 : f;
        }

    }

}