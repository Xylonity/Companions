package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.client.projectile.model.NeedleModel;
import dev.xylonity.companions.common.entity.projectile.HolinessNaginataProjectile;
import dev.xylonity.companions.common.entity.projectile.NeedleProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.joml.Quaternionf;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NeedleRenderer extends GeoEntityRenderer<NeedleProjectile> {

    public NeedleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new NeedleModel());
    }

    @Override
    protected void applyRotations(NeedleProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        Quaternionf interpolated = new Quaternionf();
        animatable.getPrevRotation().slerp(animatable.getCurrentRotation(), partialTick, interpolated);
        poseStack.mulPose(interpolated);
    }

    @Override
    public void render(NeedleProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, entity.isInWall() ? LightTexture.FULL_SKY : packedLight);
    }

}