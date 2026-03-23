package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.client.entity.model.ShadeMawModel;
import dev.xylonity.companions.common.entity.companion.ShadeMawEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShadeMawRenderer extends GeoEntityRenderer<ShadeMawEntity> {

    public ShadeMawRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShadeMawModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    protected float getDeathMaxRotation(ShadeMawEntity animatable) {
        return 0f;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ShadeMawEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (animatable.getLifetime() >= animatable.getMaxLifetime() - 124) {
            this.shadowRadius = 0.3f;
        } else {
            this.shadowRadius = 2f;
        }

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

}