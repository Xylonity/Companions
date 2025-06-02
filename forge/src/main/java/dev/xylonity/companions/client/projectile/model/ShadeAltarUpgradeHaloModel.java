package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.ShadeAltarUpgradeHaloProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadeAltarUpgradeHaloModel extends GeoModel<ShadeAltarUpgradeHaloProjectile> {

    @Override
    public ResourceLocation getModelResource(ShadeAltarUpgradeHaloProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/shade_altar_upgrade_halo.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShadeAltarUpgradeHaloProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/shade_altar_upgrade_halo.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShadeAltarUpgradeHaloProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/shade_altar_upgrade_halo.animation.json");
    }

}
