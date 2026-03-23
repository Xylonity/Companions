package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.BraceProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BraceModel extends GeoModel<BraceProjectile> {

    @Override
    public ResourceLocation getModelResource(BraceProjectile animatable) {
        return Companions.of("geo/brace.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BraceProjectile animatable) {
        return Companions.of("textures/entity/brace.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BraceProjectile animatable) {
        return Companions.of("animations/fire_mark.animation.json");
    }

}
