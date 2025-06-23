package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.client.projectile.model.HolinessNaginataModel;
import dev.xylonity.companions.common.entity.projectile.HolinessNaginataProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.joml.Quaternionf;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HolinessNaginataRenderer extends GeoEntityRenderer<HolinessNaginataProjectile> {

    public HolinessNaginataRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HolinessNaginataModel());
    }

    @Override
    protected void applyRotations(HolinessNaginataProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        Quaternionf interpolated = new Quaternionf();
        animatable.getPrevRotation().slerp(animatable.getCurrentRotation(), partialTick, interpolated);
        poseStack.mulPose(interpolated);
    }

    @Override
    public void render(HolinessNaginataProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, entity.isInWall() ? LightTexture.FULL_SKY : packedLight);
    }
}