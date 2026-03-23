package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.client.projectile.model.RedStarExplosionCenterModel;
import dev.xylonity.companions.common.entity.projectile.RedStarExplosionCenter;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RedStarExplosionCenterRenderer extends GeoEntityRenderer<RedStarExplosionCenter> {

    public RedStarExplosionCenterRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RedStarExplosionCenterModel());
    }

    @Override
    protected void applyRotations(RedStarExplosionCenter animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        double pivotY = animatable.getBbHeight() / 2.0;

        poseStack.translate(0, pivotY, 0);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - camera.getYRot()));

        poseStack.translate(0, -pivotY, 0);

        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
    }

    @Override
    public void render(@NotNull RedStarExplosionCenter entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1.5f, 1.5f, 1.5f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, LightTexture.pack(15, 15));
    }

}