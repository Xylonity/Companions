package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.client.projectile.model.RespawnTotemRingModel;
import dev.xylonity.companions.client.projectile.model.ShadeAltarUpgradeHaloModel;
import dev.xylonity.companions.common.entity.projectile.RespawnTotemRingProjectile;
import dev.xylonity.companions.common.entity.projectile.ShadeAltarUpgradeHaloProjectile;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class RespawnTotemRingRenderer extends GeoEntityRenderer<RespawnTotemRingProjectile> {

    public RespawnTotemRingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RespawnTotemRingModel());
    }

    @Override
    public void render(RespawnTotemRingProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(5, 5, 5);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, LightTexture.pack(15, 15));
    }

    @Override
    public RenderType getRenderType(RespawnTotemRingProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(getTextureLocation(animatable));
    }
}