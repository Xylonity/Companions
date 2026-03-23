package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.trigger.LaserTriggerProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LaserModel extends GeoModel<LaserTriggerProjectile> {

    @Override
    public ResourceLocation getModelResource(LaserTriggerProjectile animatable) {
        return Companions.of("geo/generic.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LaserTriggerProjectile animatable) {
        return Companions.of("textures/entity/generic.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LaserTriggerProjectile animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}
