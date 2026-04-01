package dev.xylonity.companions.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.RecallPlatformBlockEntity;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.common.tesla.ConnectionTarget;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * Unified render layer for all Tesla blockentities.
 *
 * Supports two frame modes:
 * - PULSE: one-shot frame from animationStartTick (for coils and such, used with the animated electric arc texture)
 * - LOOP: looping frame from gameTime (for pillars, used with the static electric arc texture)
 */
public class ElectricConnectionLayer<T extends AbstractTeslaBlockEntity> extends GeoRenderLayer<T> implements ITeslaUtil {

    public enum FrameMode {
        PULSE, LOOP
    }

    private final ResourceLocation texture;
    private final int totalFrames;
    private final int ticksPerFrame;
    private final FrameMode frameMode;

    public ElectricConnectionLayer(GeoRenderer<T> renderer, ResourceLocation texture, int totalFrames, int ticksPerFrame, FrameMode frameMode) {
        super(renderer);
        this.texture = texture;
        this.totalFrames = totalFrames;
        this.ticksPerFrame = ticksPerFrame;
        this.frameMode = frameMode;
    }

    public ElectricConnectionLayer(GeoRenderer<T> renderer, ResourceLocation texture, int totalFrames, int ticksPerFrame) {
        this(renderer, texture, totalFrames, ticksPerFrame, FrameMode.PULSE);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!animatable.isActive()) {
            return;
        }

        final int frame = calculateFrame(animatable);
        if (frame < 0) {
            return;
        }

        for (final ConnectionTarget target : animatable.getOutgoing()) {
            if (target == null) {
                continue;
            }

            if (target.isEntity()) {
                renderEntityConnection(poseStack, bufferSource, animatable, target, frame, packedLight);
            }
            else if (target.isBlock()) {
                renderBlockConnection(poseStack, bufferSource, animatable, target, frame, packedLight);
            }

        }

    }

    private void renderEntityConnection(PoseStack poseStack, MultiBufferSource bufferSource, T animatable, ConnectionTarget target, int frame, int packedLight) {
        final Entity entity = CompanionsEntityTracker.getEntityByUUID(target.entityId());
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        final Vec3 offset = new Vec3(0.0D, 1.25D, 0.0D);
        final Vec3 direction = livingEntity.position()
                .subtract(animatable.getBlockPos().getCenter())
                .add(0.0D, livingEntity.getBbHeight() * 1.1D, 0.0D);

        ElectricArcRenderer.renderArc(bufferSource, poseStack, offset, direction, frame, totalFrames, texture, true, packedLight);
    }

    private void renderBlockConnection(PoseStack poseStack, MultiBufferSource bufferSource, T animatable, ConnectionTarget target, int frame, int packedLight) {
        if (animatable.getLevel() == null) {
            return;
        }

        final BlockEntity rawBlockEntity = animatable.getLevel().getBlockEntity(target.blockPos());
        if (!(rawBlockEntity instanceof AbstractTeslaBlockEntity blockEntity)) {
            return;
        }

        final Vec3 offset = animatable.electricalChargeOriginOffset();
        final Vec3 blockCenter = blockEntity.getBlockPos().getCenter();

        Vec3 endOffset;
        if (blockEntity instanceof RecallPlatformBlockEntity) {
            endOffset = calculateClosestFaceOffset(animatable.getBlockPos().getCenter(), blockCenter);
        }
        else {
            endOffset = blockEntity.electricalChargeEndOffset();
        }

        final Vec3 direction = blockCenter.subtract(animatable.getBlockPos().getCenter()).add(endOffset);
        final boolean fullBright = (frameMode == FrameMode.PULSE);

        ElectricArcRenderer.renderArc(bufferSource, poseStack, offset, direction, frame, totalFrames, texture, fullBright, packedLight);
    }

    private int calculateFrame(T animatable) {
        return switch (frameMode) {
            case PULSE -> {
                int i = animatable.getAnimationStartTick() / ticksPerFrame;
                yield (i >= totalFrames) ? -1 : i;
            }
            case LOOP -> {
                if (animatable.getLevel() == null) {
                    yield 0;
                }

                final long elapsed = animatable.getLevel().getGameTime() - animatable.getAnimationStartTick();
                yield (int) (elapsed / ticksPerFrame) % totalFrames + 1;
            }

        };

    }

    private Vec3 calculateClosestFaceOffset(Vec3 sourcePos, Vec3 targetPos) {
        final Vec3[] faces = {
                new Vec3(0.0, -0.45, -0.5),
                new Vec3(0.0, 0.0, 0.5),
                new Vec3(0.5, 0.0, 0.0),
                new Vec3(-0.5, 0.0, 0.0),
        };

        Vec3 closest = Vec3.ZERO;
        double minDist = Double.MAX_VALUE;
        for (Vec3 face : faces) {
            final double distanceTo = sourcePos.distanceTo(targetPos.add(face));
            if (distanceTo < minDist) {
                minDist = distanceTo;
                closest = face;
            }

        }

        return closest;
    }

}