package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.HolinessStartProjectile;
import dev.xylonity.companions.common.entity.projectile.HolinessStartProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HolinessStarModel extends GeoModel<HolinessStartProjectile> {

    @Override
    public ResourceLocation getModelResource(HolinessStartProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/star.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HolinessStartProjectile animatable) {
        if (animatable.isFire()) {
            return new ResourceLocation(Companions.MOD_ID, "textures/entity/red_star.png");
        }

        return new ResourceLocation(Companions.MOD_ID, "textures/entity/blue_star.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HolinessStartProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/star.animation.json");
    }

}
