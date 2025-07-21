package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.MagicRayCircleProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MagicRayCircleModel extends GeoModel<MagicRayCircleProjectile> {

    @Override
    public ResourceLocation getModelResource(MagicRayCircleProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/magic_ray_circle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MagicRayCircleProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/entity/magic_ray_circle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MagicRayCircleProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "animations/magic_ray_circle.animation.json");
    }

}
