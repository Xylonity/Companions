package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.trigger.FireRayBeamEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FireRayBeamModel extends GeoModel<FireRayBeamEntity> {

    @Override
    public ResourceLocation getModelResource(FireRayBeamEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/generic.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FireRayBeamEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/generic.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FireRayBeamEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}
