package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.client.projectile.model.ShadeAltarUpgradeHaloModel;
import dev.xylonity.companions.common.entity.projectile.PontiffFireRingProjectile;
import dev.xylonity.companions.common.entity.projectile.ShadeAltarUpgradeHaloProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShadeAltarUpgradeHaloRenderer extends GeoEntityRenderer<ShadeAltarUpgradeHaloProjectile> {

    public ShadeAltarUpgradeHaloRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShadeAltarUpgradeHaloModel());
    }

    @Override
    public RenderType getRenderType(ShadeAltarUpgradeHaloProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(getTextureLocation(animatable));
    }
}