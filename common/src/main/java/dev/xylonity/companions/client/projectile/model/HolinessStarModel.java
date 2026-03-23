package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.HolinessStartProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HolinessStarModel extends GeoModel<HolinessStartProjectile> {

    @Override
    public ResourceLocation getModelResource(HolinessStartProjectile animatable) {
        if (animatable.isRed()) {
            return Companions.of("geo/red_star.geo.json");
        }

        return Companions.of("geo/blue_star.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HolinessStartProjectile animatable) {
        if (animatable.isRed()) {
            return Companions.of("textures/entity/red_star.png");
        }

        return Companions.of("textures/entity/blue_star.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HolinessStartProjectile animatable) {
        return Companions.of("animations/star.animation.json");
    }

}
