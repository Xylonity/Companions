package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.BlueStarExplosion;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlueStarExplosionModel extends GeoModel<BlueStarExplosion> {

    @Override
    public ResourceLocation getModelResource(BlueStarExplosion animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/blue_star_explosion.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlueStarExplosion animatable) {
        int frames = 14;
        int perTick = 1;

        int frameIndex = (animatable.tickCount / perTick) % frames;
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, String.format("textures/entity/blue_star_explosion_center_%d.png", frameIndex));
    }

    @Override
    public ResourceLocation getAnimationResource(BlueStarExplosion animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "animations/generic.animation.json");
    }

}
