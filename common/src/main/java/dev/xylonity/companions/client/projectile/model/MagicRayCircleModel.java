package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.MagicRayCircleProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MagicRayCircleModel extends GeoModel<MagicRayCircleProjectile> {

    @Override
    public ResourceLocation getModelResource(MagicRayCircleProjectile animatable) {
        return Companions.of("geo/magic_ray_circle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MagicRayCircleProjectile animatable) {
        return Companions.of("textures/entity/magic_ray_circle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MagicRayCircleProjectile animatable) {
        return Companions.of("animations/magic_ray_circle.animation.json");
    }

}
