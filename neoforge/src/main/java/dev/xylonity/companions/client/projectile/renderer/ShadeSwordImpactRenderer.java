package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.client.projectile.model.ShadeSwordImpactModel;
import dev.xylonity.companions.common.entity.projectile.ShadeSwordImpactProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShadeSwordImpactRenderer extends GeoEntityRenderer<ShadeSwordImpactProjectile> {

    public ShadeSwordImpactRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShadeSwordImpactModel());
    }

    @Override
    protected void applyRotations(ShadeSwordImpactProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180 + animatable.getYRot()));

        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
    }

    @Override
    public void render(@NotNull ShadeSwordImpactProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1.8f, 1.8f, 1.8f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

}