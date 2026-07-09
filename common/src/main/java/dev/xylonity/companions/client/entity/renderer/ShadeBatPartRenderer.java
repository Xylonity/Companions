package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xylonity.companions.client.entity.model.ShadeBatPartModel;
import dev.xylonity.companions.common.entity.companion.ShadeBatEntity;
import dev.xylonity.companions.common.entity.companion.ShadeBatPartEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShadeBatPartRenderer extends GeoEntityRenderer<ShadeBatPartEntity> {

    public ShadeBatPartRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShadeBatPartModel());
        this.shadowRadius = 0.15f;
    }

    @Override
    protected void applyRotations(ShadeBatPartEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        final Vec3 movement = getRenderMovementDirection(animatable, partialTick);
        if (movement.lengthSqr() <= 1.0E-6) {
            return;
        }

        final double horizontal = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
        final float yaw = (float) (Math.atan2(movement.z, movement.x) * (180F / Math.PI)) - 90.0F;
        final float pitch = (float) (Math.atan2(movement.y, horizontal) * (180F / Math.PI));

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
    }

    private Vec3 getRenderMovementDirection(ShadeBatPartEntity animatable, float partialTick) {
        final ShadeBatEntity parent = animatable.getParent();
        if (parent != null && !parent.isRemoved() && !parent.isBlood()) {
            final LivingEntity target = parent.getShadeBatTarget();
            final Vec3 renderPosition = animatable.getPosition(partialTick);
            if (target != null) {
                return target.getEyePosition(partialTick).subtract(renderPosition);
            }

            final Vec3 targetPosition = parent.getBatPartRenderPosition(animatable.getPartIndex(), partialTick);
            final Vec3 toTargetPosition = targetPosition.subtract(renderPosition);
            return toTargetPosition.lengthSqr() > 1.0E-4 ? toTargetPosition : parent.getBatPartMovementDirection(animatable.getPartIndex(), partialTick);
        }

        final Vec3 currentLook = animatable.getLookAngle();
        return currentLook.lengthSqr() > 1.0E-6 ? currentLook : Vec3.ZERO;
    }

    @Override
    public RenderType getRenderType(ShadeBatPartEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getGeoModel().getTextureResource(animatable));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ShadeBatPartEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        final float fade = animatable.getSpawnFade(partialTick);
        if (fade <= 0.0F) {
            return;
        }

        poseStack.scale(0.3f, 0.3f, 0.3f);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha * fade);
    }

    @Override
    protected float getDeathMaxRotation(ShadeBatPartEntity animatable) {
        return 0f;
    }

}