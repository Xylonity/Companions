package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.BlueStarExplosionCenter;
import dev.xylonity.companions.common.entity.projectile.RedStarExplosionCenter;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlueStarExplosionCenterModel extends GeoModel<BlueStarExplosionCenter> {

    @Override
    public ResourceLocation getModelResource(BlueStarExplosionCenter animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/blue_star_explosion_center.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlueStarExplosionCenter animatable) {
        int frames = 14;
        int perTick = 1;

        int frameIndex = (animatable.tickCount / perTick) % frames;
        return new ResourceLocation(Companions.MOD_ID, String.format("textures/entity/blue_star_explosion_center_%d.png", frameIndex));
    }

    @Override
    public ResourceLocation getAnimationResource(BlueStarExplosionCenter animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}
