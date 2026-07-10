package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.FireMarkProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FireMarkModel extends GeoModel<FireMarkProjectile> {

    @Override
    public ResourceLocation getModelResource(FireMarkProjectile animatable) {
        return Companions.of("geo/fire_mark.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FireMarkProjectile animatable) {
        return Companions.of("textures/entity/fire_mark.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FireMarkProjectile animatable) {
        return Companions.of("animations/fire_mark.animation.json");
    }

}
