package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.trigger.GenericTriggerProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericTriggerProjectileModel<T extends GenericTriggerProjectile> extends GeoModel<T> {

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/generic.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/generic.png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}
