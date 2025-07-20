package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.client.projectile.model.HolinessStarModel;
import dev.xylonity.companions.common.entity.projectile.HolinessStartProjectile;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HolinessStarRenderer extends GeoEntityRenderer<HolinessStartProjectile> {

    public HolinessStarRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HolinessStarModel());
    }

    @Override
    public void render(@NotNull HolinessStartProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(2.5f, 2.5f, 2.5f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected void applyRotations(HolinessStartProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        double pivotY = animatable.getBbHeight() / 2.0;

        poseStack.translate(0, pivotY, 0);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - camera.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(-camera.getXRot()));

        poseStack.translate(0, -pivotY, 0);

        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
    }

}