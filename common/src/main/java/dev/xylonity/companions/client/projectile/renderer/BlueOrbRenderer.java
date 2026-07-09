package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xylonity.companions.client.projectile.model.BlueOrbModel;
import dev.xylonity.companions.common.entity.projectile.BlueOrbProjectile;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BlueOrbRenderer extends GeoEntityRenderer<BlueOrbProjectile> {

    public BlueOrbRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BlueOrbModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, BlueOrbProjectile animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (isReRender) {
            super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }

        poseStack.scale(1.15f, 1.15f, 1.15f);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        final float pulse = 1f + 0.2f * Mth.sin((animatable.tickCount + partialTick) * 0.4f);

        poseStack.pushPose();
        poseStack.scale(1.4f * pulse, 1.4f * pulse, 1.4f);
        reRender(model, poseStack, bufferSource, animatable, renderType, bufferSource.getBuffer(renderType), partialTick, packedLight, packedOverlay, red, green, blue, 0.35f);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.scale(2f * pulse, 2f * pulse, 1.4f);
        reRender(model, poseStack, bufferSource, animatable, renderType, bufferSource.getBuffer(renderType), partialTick, packedLight, packedOverlay, red, green, blue, 0.12f);
        poseStack.popPose();
    }

    @Override
    protected void applyRotations(BlueOrbProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        final double pivotY = animatable.getBbHeight() / 2.0;

        poseStack.translate(0, pivotY, 0);

        final Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - camera.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(-camera.getXRot()));

        poseStack.translate(0, -pivotY, 0);

        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
    }

    @Override
    public RenderType getRenderType(BlueOrbProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(getGeoModel().getTextureResource(animatable));
    }

}
