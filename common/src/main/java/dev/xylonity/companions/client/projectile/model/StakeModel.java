package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.StakeProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StakeModel extends GeoModel<StakeProjectile> {

    @Override
    public ResourceLocation getModelResource(StakeProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/stake.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StakeProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/stake.png");
    }

    @Override
    public ResourceLocation getAnimationResource(StakeProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}
