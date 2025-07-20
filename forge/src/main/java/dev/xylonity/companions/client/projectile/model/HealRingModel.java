package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.HealRingProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HealRingModel extends GeoModel<HealRingProjectile> {

    @Override
    public ResourceLocation getModelResource(HealRingProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/heal_ring.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HealRingProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/entity/heal_ring.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HealRingProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "animations/heal_ring.animation.json");
    }

}
