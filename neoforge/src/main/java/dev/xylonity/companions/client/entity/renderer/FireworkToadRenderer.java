package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.client.entity.model.FireworkToadModel;
import dev.xylonity.companions.common.entity.summon.FireworkToadEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireworkToadRenderer extends GeoEntityRenderer<FireworkToadEntity> {

    public FireworkToadRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireworkToadModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    protected void applyRotations(FireworkToadEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        Vec3 center = animatable.getParabolaCenter();
        if (center != null) {
            Vec3 look = center.subtract(animatable.position()).normalize();

            poseStack.translate(0.0, animatable.getBbHeight() / 2.0f, 0.0);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) (Math.toDegrees(Math.atan2(look.z, look.x)) - 90.0)));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (-Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))))));
            poseStack.translate(0.0, -animatable.getBbHeight() / 2.0f, 0.0);
        } else {
            super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
        }
    }

}
