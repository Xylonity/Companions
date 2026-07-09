package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.ShadeAltarUpgradeHaloProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadeAltarUpgradeHaloModel extends GeoModel<ShadeAltarUpgradeHaloProjectile> {

    @Override
    public ResourceLocation getModelResource(ShadeAltarUpgradeHaloProjectile animatable) {
        return Companions.of("geo/shade_altar_upgrade_halo.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShadeAltarUpgradeHaloProjectile animatable) {
        int frames = 8;
        int perTick = 2;

        int frameIndex = (animatable.tickCount / perTick) % frames;
        return Companions.of(String.format("textures/entity/shade_altar_upgrade_halo_%d.png", frameIndex));
    }

    @Override
    public ResourceLocation getAnimationResource(ShadeAltarUpgradeHaloProjectile animatable) {
        return Companions.of("animations/shade_altar_upgrade_halo.animation.json");
    }

}
