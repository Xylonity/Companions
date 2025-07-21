package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.projectile.model.MagicRayCircleModel;
import dev.xylonity.companions.common.entity.projectile.MagicRayCircleProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class MagicRayCircleRenderer extends GeoEntityRenderer<MagicRayCircleProjectile> {

    public MagicRayCircleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MagicRayCircleModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MagicRayCircleProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/magic_ray_circle.png");
    }

    @Override
    public RenderType getRenderType(MagicRayCircleProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(MagicRayCircleProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        poseStack.scale(2, 2, 2);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    @Override
    protected void applyRotations(MagicRayCircleProjectile entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick);
        float yaw   = Mth.rotLerp(partialTick, entity.getYaw(),   entity.getYaw());
        float pitch = Mth.lerp(partialTick,    entity.getPitch(), entity.getPitch());

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
    }

}