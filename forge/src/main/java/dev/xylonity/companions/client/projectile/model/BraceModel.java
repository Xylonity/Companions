package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.CompanionsForge;
import dev.xylonity.companions.common.entity.projectile.BraceProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BraceModel extends GeoModel<BraceProjectile> {

    @Override
    public ResourceLocation getModelResource(BraceProjectile animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "geo/brace.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BraceProjectile animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "textures/entity/brace.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BraceProjectile animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "animations/fire_mark.animation.json");
    }

}
