package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.client.entity.model.ShadeBatModel;
import dev.xylonity.companions.common.entity.companion.ShadeBatEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShadeBatRenderer extends GeoEntityRenderer<ShadeBatEntity> {

    public ShadeBatRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShadeBatModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public RenderType getRenderType(ShadeBatEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getGeoModel().getTextureResource(animatable));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ShadeBatEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        final float fade = animatable.getSpawnFade(partialTick);
        if (fade <= 0.0F) {
            return;
        }

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha * fade);
    }

    @Override
    protected float getDeathMaxRotation(ShadeBatEntity animatable) {
        return 0f;
    }

}
