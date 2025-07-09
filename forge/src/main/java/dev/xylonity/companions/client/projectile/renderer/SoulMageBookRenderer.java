package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.projectile.model.SoulMageBookModel;
import dev.xylonity.companions.common.entity.projectile.SoulMageBookEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SoulMageBookRenderer extends GeoEntityRenderer<SoulMageBookEntity> {

    public SoulMageBookRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SoulMageBookModel());
        this.addRenderLayer(new SoulMageBookGlowLayer(this));
    }

    @Override
    public void render(@NotNull SoulMageBookEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, entity.isInWall() ? LightTexture.FULL_SKY : packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SoulMageBookEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/soul_mage_book.png");
    }

    static class SoulMageBookGlowLayer extends GeoRenderLayer<SoulMageBookEntity> {
        private static final ResourceLocation EMB_TEXTURE = new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/soul_mage_book_glow.png");

        public SoulMageBookGlowLayer(GeoRenderer<SoulMageBookEntity> entityRenderer) {
            super(entityRenderer);
        }

        @Override
        public void render(PoseStack poseStack, SoulMageBookEntity entity, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            RenderType glowRenderType = RenderType.entityTranslucent(EMB_TEXTURE);

            float r = entity.getCurrentRed() / 255.0f;
            float g = entity.getCurrentGreen() / 255.0f;
            float b = entity.getCurrentBlue() / 255.0f;

            VertexConsumer glowBuffer = bufferSource.getBuffer(glowRenderType);

            getRenderer().reRender(bakedModel, poseStack, bufferSource, entity, glowRenderType, glowBuffer, partialTick, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1.0f);
        }
    }

}