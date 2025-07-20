package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.CompanionsCommon;
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
    public void render(@NotNull FloorCakeCreamProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(entity.getSize(), entity.getSize(), entity.getSize());

        BakedGeoModel model = this.model.getBakedModel(this.model.getModelResource(animatable));

        RenderType renderType = RenderType.entityTranslucent(getTextureLocation(entity));
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);

        reRender(model, poseStack, bufferSource, entity, renderType, vertexConsumer, partialTick, packedLight, getPackedOverlay(entity, 0), 1.0F, 1.0F, 1.0F, alpha(entity));
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