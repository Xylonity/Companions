package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.client.projectile.model.HolyRingModel;
import dev.xylonity.companions.common.entity.projectile.HolyRingProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HolyRingRenderer extends GeoEntityRenderer<HolyRingProjectile> {

    public HolyRingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HolyRingModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, HolyRingProjectile animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        final float age = animatable.tickCount + partialTick;
        final float scale = Math.max(0.1f, HolyRingProjectile.radius(age) / 1.5f);
        final float fade = 1f - Mth.clamp((age - (HolyRingProjectile.RING_LIFETIME - 8)) / 8f, 0f, 1f);

        poseStack.scale(scale, 1f, scale);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, LightTexture.pack(15, 15), packedOverlay, red, green, blue, 0.9f * fade);
    }

    @Override
    public RenderType getRenderType(HolyRingProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(getGeoModel().getTextureResource(animatable));
    }

}
