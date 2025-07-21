package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.TornadoProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TornadoModel extends GeoModel<TornadoProjectile> {

    @Override
    public ResourceLocation getModelResource(TornadoProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/ice_tornado.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TornadoProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/entity/ice_tornado.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TornadoProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "animations/ice_tornado.animation.json");
    }

}
