package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.projectile.model.MagicRayPieceModel;
import dev.xylonity.companions.common.entity.projectile.MagicRayPieceProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MagicRayPieceRenderer extends GeoEntityRenderer<MagicRayPieceProjectile> {

    public MagicRayPieceRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MagicRayPieceModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MagicRayPieceProjectile animatable) {
        return Companions.of("textures/entity/magic_ray_piece.png");
    }

    @Override
    public RenderType getRenderType(MagicRayPieceProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(getTextureLocation(animatable));
    }

    @Override
    protected void applyRotations(MagicRayPieceProjectile entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick);

        final float yaw = Mth.rotLerp(partialTick, entity.getYaw(), entity.getYaw());
        final float pitch = Mth.lerp(partialTick, entity.getPitch(), entity.getPitch());

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
    }

}