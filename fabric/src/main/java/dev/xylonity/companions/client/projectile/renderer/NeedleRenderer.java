package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.client.projectile.model.NeedleModel;
import dev.xylonity.companions.common.entity.projectile.NeedleProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NeedleRenderer extends GeoEntityRenderer<NeedleProjectile> {

    public NeedleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new NeedleModel());
    }

    @Override
    protected void applyRotations(NeedleProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        Quaternionf interpolated = new Quaternionf();
        animatable.getPrevRotation().slerp(animatable.getCurrentRotation(), partialTick, interpolated);
        poseStack.mulPose(interpolated);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, NeedleProjectile animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, animatable.isInWall() ? LightTexture.FULL_SKY : packedLight, packedOverlay, colour);
    }
}