package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xylonity.companions.client.projectile.model.BloodSlashModel;
import dev.xylonity.companions.common.entity.projectile.BloodSlashProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BloodSlashRenderer extends GeoEntityRenderer<BloodSlashProjectile> {

    public BloodSlashRenderer(EntityRendererProvider.Context context) {
        super(context, new BloodSlashModel());
    }

    @Override
    protected void applyRotations(BloodSlashProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        Vec3 motion = animatable.getDeltaMovement();
        if (motion.lengthSqr() > 1.0E-6) {
            poseStack.mulPose(Axis.YP.rotationDegrees((float) (Mth.atan2(motion.x, motion.z) * Mth.RAD_TO_DEG)));
            poseStack.mulPose(Axis.XP.rotationDegrees(-((float) (Mth.atan2(motion.y, Mth.sqrt((float) (motion.x * motion.x + motion.z * motion.z))) * Mth.RAD_TO_DEG))));
        }

        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, BloodSlashProjectile animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        poseStack.scale(1.75f, 1.75f, 1.75f);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

}
