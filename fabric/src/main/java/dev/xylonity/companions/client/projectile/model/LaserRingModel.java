package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.LaserRingProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LaserRingModel extends GeoModel<LaserRingProjectile> {

    @Override
    public ResourceLocation getModelResource(LaserRingProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/pontiff_fire_ring.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LaserRingProjectile animatable) {
        int frames = 10;
        int perTick = 2;

        int frameIndex = (animatable.tickCount / perTick) % frames;
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, String.format("textures/entity/pontiff_fire_ring_%d.png", frameIndex));
    }

    @Override
    public ResourceLocation getAnimationResource(LaserRingProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "animations/generic.animation.json");
    }

}
