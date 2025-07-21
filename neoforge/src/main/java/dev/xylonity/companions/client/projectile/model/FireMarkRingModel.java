package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.FireMarkRingProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FireMarkRingModel extends GeoModel<FireMarkRingProjectile> {

    @Override
    public ResourceLocation getModelResource(FireMarkRingProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/fire_mark_ring.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FireMarkRingProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/entity/fire_mark_ring.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FireMarkRingProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "animations/fire_mark_ring.animation.json");
    }

}
