package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.ShadeSwordImpactProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadeSwordImpactModel extends GeoModel<ShadeSwordImpactProjectile> {

    @Override
    public ResourceLocation getModelResource(ShadeSwordImpactProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/shade_sword_impact.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShadeSwordImpactProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/shade_sword_impact.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShadeSwordImpactProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/shade_sword_impact.animation.json");
    }

}
