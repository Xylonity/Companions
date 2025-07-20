package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.projectile.model.StakeModel;
import dev.xylonity.companions.common.entity.projectile.StakeProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StakeRenderer extends GeoEntityRenderer<StakeProjectile> {

    public StakeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StakeModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull StakeProjectile animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stake.png");
    }

    @Override
    protected void applyRotations(StakeProjectile entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        Quaternionf interpolated = new Quaternionf();
        entity.getPrevRotation().slerp(entity.getCurrentRotation(), partialTicks, interpolated);
        poseStack.mulPose(interpolated);
    }

    //@Override
    //public void render(StakeProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
    //    poseStack.pushPose();
    //    super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    //    poseStack.popPose();
    //}

}