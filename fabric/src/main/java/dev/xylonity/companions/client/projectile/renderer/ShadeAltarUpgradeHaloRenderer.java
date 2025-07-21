package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.client.projectile.model.ShadeAltarUpgradeHaloModel;
import dev.xylonity.companions.common.entity.projectile.ShadeAltarUpgradeHaloProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShadeAltarUpgradeHaloRenderer extends GeoEntityRenderer<ShadeAltarUpgradeHaloProjectile> {

    public ShadeAltarUpgradeHaloRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ShadeAltarUpgradeHaloModel());
    }

}