package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
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
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireMarkRingRenderer extends GeoEntityRenderer<FireMarkRingProjectile> {

    private static final float MODEL_ORIGINAL_SIZE = 2.5F;

    public FireMarkRingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireMarkRingModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FireMarkRingProjectile animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/fire_mark_ring.png");
    }

    @Override
    public RenderType getRenderType(FireMarkRingProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(@NotNull FireMarkRingProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        float desiredRadius = (float) CompanionsConfig.FIRE_MARK_EFFECT_RADIUS;
        float scale = desiredRadius / MODEL_ORIGINAL_SIZE;

        poseStack.scale(scale, scale, scale);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

}