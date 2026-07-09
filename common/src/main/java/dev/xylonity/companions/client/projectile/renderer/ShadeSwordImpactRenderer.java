package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xylonity.companions.client.projectile.model.ShadeSwordImpactModel;
import dev.xylonity.companions.common.entity.projectile.ShadeAltarUpgradeHaloProjectile;
import dev.xylonity.companions.common.entity.projectile.ShadeSwordImpactProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
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
    public void actuallyRender(PoseStack poseStack, ShadeSwordImpactProjectile animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.scale(1.8f, 1.8f, 1.8f);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public RenderType getRenderType(ShadeSwordImpactProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(getGeoModel().getTextureResource(animatable));
    }

}