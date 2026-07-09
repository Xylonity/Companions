package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.BigIceShardProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IceShardBigModel extends GeoModel<BigIceShardProjectile> {

    @Override
    public ResourceLocation getModelResource(BigIceShardProjectile animatable) {
        return Companions.of("geo/ice_shard_big.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BigIceShardProjectile animatable) {
        return Companions.of("textures/entity/ice_shard_big.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BigIceShardProjectile animatable) {
        return Companions.of("animations/ice_shard_big.animation.json");
    }

}
