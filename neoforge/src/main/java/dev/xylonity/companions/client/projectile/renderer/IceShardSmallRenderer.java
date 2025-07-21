package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.projectile.model.IceShardSmallModel;
import dev.xylonity.companions.common.entity.projectile.SmallIceShardProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceShardSmallRenderer extends GeoEntityRenderer<SmallIceShardProjectile> {

    public IceShardSmallRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceShardSmallModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SmallIceShardProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/ice_shard_small.png");
    }

    @Override
    protected void applyRotations(SmallIceShardProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        Quaternionf interpolated = new Quaternionf();
        animatable.getPrevRotation().slerp(animatable.getCurrentRotation(), partialTick, interpolated);
        poseStack.mulPose(interpolated);

        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
    }

}
