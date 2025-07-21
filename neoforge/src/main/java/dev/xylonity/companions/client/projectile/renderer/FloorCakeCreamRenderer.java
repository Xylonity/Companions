package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.client.projectile.model.FloorCakeCreamModel;
import dev.xylonity.companions.common.entity.projectile.FloorCakeCreamProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FloorCakeCreamRenderer extends GeoEntityRenderer<FloorCakeCreamProjectile> {

    public FloorCakeCreamRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FloorCakeCreamModel());
    }

    @Override
    public RenderType getRenderType(FloorCakeCreamProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, FloorCakeCreamProjectile animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        poseStack.scale(animatable.getSize(), animatable.getSize(), animatable.getSize());

        BakedGeoModel model2 = this.model.getBakedModel(this.model.getModelResource(animatable, this));

        RenderType renderType2 = RenderType.entityTranslucent(getTextureLocation(animatable));
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType2);

        int a = Math.round(alpha(animatable) * 255);
        int rgb = 0x00FFFFFF;
        int colour2 = (a << 24) | rgb;

        reRender(model2, poseStack, bufferSource, animatable, renderType2, vertexConsumer, partialTick, packedLight, getPackedOverlay(animatable, 0, partialTick), colour2);
    }

    private float alpha(FloorCakeCreamProjectile entity) {
        int fadeIn = 10;
        int fadeOut = entity.getLifetime() - 10;

        if (entity.tickCount <= fadeIn) {
            return entity.tickCount / (float)fadeIn;
        } else if (entity.tickCount >= fadeOut) {
            float fadeOutProgress = (entity.tickCount - fadeOut) / (float)(entity.getLifetime() - fadeOut);
            return 1.0f - fadeOutProgress;
        } else {
            return 1.0f;
        }
    }

}