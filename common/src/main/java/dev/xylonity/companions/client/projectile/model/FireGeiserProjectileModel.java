package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.FireGeiserProjectile;
import dev.xylonity.companions.common.entity.projectile.trigger.LaserTriggerProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FireGeiserProjectileModel extends GeoModel<FireGeiserProjectile> {

    @Override
    public ResourceLocation getModelResource(FireGeiserProjectile animatable) {
        return Companions.of("geo/fire_geiser.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FireGeiserProjectile animatable) {
        return Companions.of("textures/block/eternal_fire.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FireGeiserProjectile animatable) {
        return Companions.of("animations/fire_geiser.animation.json");
    }

}
