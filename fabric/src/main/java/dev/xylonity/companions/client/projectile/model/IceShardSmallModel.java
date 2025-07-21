package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.SmallIceShardProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IceShardSmallModel extends GeoModel<SmallIceShardProjectile> {

    @Override
    public ResourceLocation getModelResource(SmallIceShardProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/ice_shard_small.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SmallIceShardProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/entity/ice_shard_small.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SmallIceShardProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "animations/ice_shard_small.animation.json");
    }

}
