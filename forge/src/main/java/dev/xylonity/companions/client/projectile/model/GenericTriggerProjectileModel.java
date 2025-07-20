package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.BlackHoleProjectile;
import dev.xylonity.companions.common.entity.projectile.trigger.GenericTriggerProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericTriggerProjectileModel extends GeoModel<GenericTriggerProjectile> {

    @Override
    public ResourceLocation getModelResource(GenericTriggerProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/generic.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GenericTriggerProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/generic.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GenericTriggerProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}
