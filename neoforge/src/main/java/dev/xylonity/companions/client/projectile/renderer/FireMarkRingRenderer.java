package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.projectile.model.FireMarkRingModel;
import dev.xylonity.companions.common.entity.projectile.FireMarkRingProjectile;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireMarkRingRenderer extends GeoEntityRenderer<FireMarkRingProjectile> {

    private static final float MODEL_ORIGINAL_SIZE = 2.5F;

    public FireMarkRingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireMarkRingModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FireMarkRingProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/fire_mark_ring.png");
    }

    @Override
    public RenderType getRenderType(FireMarkRingProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, FireMarkRingProjectile animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        float desiredRadius = (float) CompanionsConfig.FIRE_MARK_EFFECT_RADIUS;
        float scale = desiredRadius / MODEL_ORIGINAL_SIZE;

        poseStack.scale(scale, scale, scale);

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

}