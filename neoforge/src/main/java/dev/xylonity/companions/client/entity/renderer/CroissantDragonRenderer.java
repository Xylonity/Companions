package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.client.entity.model.CroissantDragonModel;
import dev.xylonity.companions.common.entity.companion.CroissantDragonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CroissantDragonRenderer extends GeoEntityRenderer<CroissantDragonEntity> {

    public CroissantDragonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CroissantDragonModel());
        this.shadowRadius = 1.4f;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, CroissantDragonEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (animatable.getEatenAmount() >= 1 && bone.getName().equals("eat3")) {
            return;
        }

        if (animatable.getEatenAmount() == 2 && bone.getName().equals("eat4")) {
            return;
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public void render(CroissantDragonEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float scale = entity.getMilkAmount() == 0 ? 0.3f
                : entity.getMilkAmount() == 1 ? 0.5f
                : entity.getMilkAmount() == 2 ? 0.7f
                : 1f;
        poseStack.scale(scale, scale, scale);

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}