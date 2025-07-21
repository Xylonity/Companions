package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.BloodTornadoProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BloodTornadoModel extends GeoModel<BloodTornadoProjectile> {

    @Override
    public ResourceLocation getModelResource(BloodTornadoProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/blood_tornado.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BloodTornadoProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/entity/blood_tornado.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BloodTornadoProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "animations/blood_tornado.animation.json");
    }

}
