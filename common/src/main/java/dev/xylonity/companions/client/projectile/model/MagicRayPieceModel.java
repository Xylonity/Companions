package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.MagicRayPieceProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MagicRayPieceModel extends GeoModel<MagicRayPieceProjectile> {

    @Override
    public ResourceLocation getModelResource(MagicRayPieceProjectile animatable) {
        return Companions.of("geo/magic_ray_piece.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MagicRayPieceProjectile animatable) {
        return Companions.of("textures/entity/magic_ray_piece.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MagicRayPieceProjectile animatable) {
        return Companions.of("animations/magic_ray_piece.animation.json");
    }

}
