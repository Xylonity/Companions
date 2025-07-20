package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.FireRayPieceProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FireRayPieceModel extends GeoModel<FireRayPieceProjectile> {

    @Override
    public ResourceLocation getModelResource(FireRayPieceProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/fire_ray_piece.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FireRayPieceProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/entity/fire_ray_piece.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FireRayPieceProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "animations/fire_ray_piece.animation.json");
    }

}
