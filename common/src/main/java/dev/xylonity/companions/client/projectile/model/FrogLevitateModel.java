package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.FrogLevitateProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FrogLevitateModel extends GeoModel<FrogLevitateProjectile> {

    @Override
    public ResourceLocation getModelResource(FrogLevitateProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/frog_levitate.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FrogLevitateProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/frog_levitate.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FrogLevitateProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/frog_levitate.animation.json");
    }

}
