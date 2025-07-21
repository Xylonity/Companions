package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.ScrollProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ScrollModel extends GeoModel<ScrollProjectile> {

    @Override
    public ResourceLocation getModelResource(ScrollProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/scroll.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ScrollProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/entity/scroll.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ScrollProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "animations/scroll.animation.json");
    }

}
