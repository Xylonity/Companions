package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.BloodTornadoProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BloodTornadoModel extends GeoModel<BloodTornadoProjectile> {

    @Override
    public ResourceLocation getModelResource(BloodTornadoProjectile animatable) {
        return Companions.of("geo/blood_tornado.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BloodTornadoProjectile animatable) {
        return Companions.of("textures/entity/blood_tornado.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BloodTornadoProjectile animatable) {
        return Companions.of("animations/blood_tornado.animation.json");
    }

}
