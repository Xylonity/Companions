package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.FrogEggProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FrogEggModel extends GeoModel<FrogEggProjectile> {

    @Override
    public ResourceLocation getModelResource(FrogEggProjectile animatable) {
        return Companions.of("geo/frog_egg.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FrogEggProjectile animatable) {
        return Companions.of("textures/entity/frog_egg.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FrogEggProjectile animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}
