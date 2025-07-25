package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.client.projectile.model.BloodSlashModel;
import dev.xylonity.companions.common.entity.projectile.BlackHoleProjectile;
import dev.xylonity.companions.common.entity.projectile.BloodSlashProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BloodSlashRenderer extends GeoEntityRenderer<BloodSlashProjectile> {

    public BloodSlashRenderer(EntityRendererProvider.Context context) {
        super(context, new BloodSlashModel());
    }

    @Override
    protected void applyRotations(BloodSlashProjectile entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        Vec3 motion = entity.getDeltaMovement();
        if (motion.lengthSqr() > 1.0E-6) {
            poseStack.mulPose(Axis.YP.rotationDegrees((float) (Mth.atan2(motion.x, motion.z) * Mth.RAD_TO_DEG)));
            poseStack.mulPose(Axis.XP.rotationDegrees(-((float) (Mth.atan2(motion.y, Mth.sqrt((float) (motion.x * motion.x + motion.z * motion.z))) * Mth.RAD_TO_DEG))));
        }

        super.applyRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public void render(@NotNull BloodSlashProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1.75f, 1.75f, 1.75f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public RenderType getRenderType(BloodSlashProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(getTextureLocation(animatable));
    }
}
