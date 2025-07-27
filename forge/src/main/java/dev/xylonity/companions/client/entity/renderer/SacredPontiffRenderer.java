package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.client.entity.model.SacredPontiffModel;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SacredPontiffRenderer extends GeoEntityRenderer<SacredPontiffEntity> {

    public SacredPontiffRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SacredPontiffModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 2f;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, SacredPontiffEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float radius;
        radius = switch (animatable.getState()) {
            case 0, 1, 2 -> 2f;
            case 4 -> 0f;
            default -> 1f;
        };
        this.shadowRadius = radius;
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected float getDeathMaxRotation(SacredPontiffEntity animatable) {
        return 0f;
    }

    @Override
    public int getPackedOverlay(SacredPontiffEntity animatable, float u) {
        if (animatable.getState() >= 5 && animatable.isDeadOrDying()) {
            return OverlayTexture.NO_OVERLAY;
        }

        return super.getPackedOverlay(animatable, u);
    }

}