package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.knightlib.api.util.KnightLibColor;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xylonity.companions.client.projectile.model.HolinessStarModel;
import dev.xylonity.companions.common.entity.projectile.HealRingProjectile;
import dev.xylonity.companions.common.entity.projectile.HolinessStartProjectile;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HolinessStarRenderer extends GeoEntityRenderer<HolinessStartProjectile> {

    public HolinessStarRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HolinessStarModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, HolinessStartProjectile animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (isReRender) {
            super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
            return;
        }

        poseStack.scale(2.5f, 2.5f, 2.5f);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, colour);

        final float pulse = 1f + 0.2f * Mth.sin((animatable.tickCount + partialTick) * 0.35f);

        poseStack.pushPose();
        poseStack.scale(1.45f * pulse, 1.45f * pulse, 1f);
        reRender(model, poseStack, bufferSource, animatable, renderType, bufferSource.getBuffer(renderType), partialTick, packedLight, packedOverlay, KnightLibColor.withAlpha(colour, (int) (0.35f * 255)));
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.scale(2.1f * pulse, 2.1f * pulse, 1f);
        reRender(model, poseStack, bufferSource, animatable, renderType, bufferSource.getBuffer(renderType), partialTick, packedLight, packedOverlay, KnightLibColor.withAlpha(colour, (int) (0.12f * 255)));
        poseStack.popPose();
    }

    @Override
    protected void applyRotations(HolinessStartProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        double pivotY = animatable.getBbHeight() / 2.0;

        poseStack.translate(0, pivotY, 0);

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - camera.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(-camera.getXRot()));

        poseStack.translate(0, -pivotY, 0);

        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
    }

    @Override
    public RenderType getRenderType(HolinessStartProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(getGeoModel().getTextureResource(animatable));
    }

}