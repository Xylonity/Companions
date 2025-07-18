package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.projectile.model.FireMarkModel;
import dev.xylonity.companions.common.entity.projectile.FireMarkProjectile;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireMarkRenderer extends GeoEntityRenderer<FireMarkProjectile> {

    private static final float MAX_PITCH_ANGLE = 5.0F;

    public FireMarkRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireMarkModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FireMarkProjectile animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/fire_mark.png");
    }

    @Override
    public RenderType getRenderType(FireMarkProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    protected void applyRotations(FireMarkProjectile entity, PoseStack poseStack,
                                  float ageInTicks, float rotationYaw, float partialTicks) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (camera != null) {
            double dx = camera.getPosition().x - entity.getX();
            double dz = camera.getPosition().z - entity.getZ();

            if (Math.abs(dx) < 1e-4 && Math.abs(dz) < 1e-4) {
                dx = 0;
                dz = 1;
            }

            float desiredYaw = (float) Math.toDegrees(Math.atan2(dx, dz));
            float yawCorrection = 0.0F;
            desiredYaw += yawCorrection;

            double dy = camera.getPosition().y - entity.getY();
            double horizontalDist = Math.sqrt(dx * dx + dz * dz);
            float rawPitch = (float) Math.toDegrees(Math.atan2(dy, horizontalDist));
            float pitchFactor = 0.5F;
            float desiredPitch = rawPitch * pitchFactor;
            float maxPitch = 5.0F;
            if (desiredPitch > maxPitch) desiredPitch = maxPitch;
            if (desiredPitch < -maxPitch) desiredPitch = -maxPitch;

            poseStack.mulPose(Axis.YP.rotationDegrees(desiredYaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(desiredPitch));
        }
        super.applyRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
    }

}