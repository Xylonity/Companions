package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.client.projectile.model.FireRayPieceModel;
import dev.xylonity.companions.common.entity.projectile.FireRayPieceProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireRayPieceRenderer extends GeoEntityRenderer<FireRayPieceProjectile> {

    public FireRayPieceRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireRayPieceModel());
    }

    @Override
    protected void applyRotations(FireRayPieceProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        float yaw = Mth.rotLerp(partialTick, animatable.getPieceYaw(), animatable.getPieceYaw());
        float pitch = Mth.lerp(partialTick, animatable.getPiecePitch(), animatable.getPiecePitch());

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
    }

}