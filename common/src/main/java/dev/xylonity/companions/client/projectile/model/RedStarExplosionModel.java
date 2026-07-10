package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.RedStarExplosion;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RedStarExplosionModel extends GeoModel<RedStarExplosion> {

    @Override
    public ResourceLocation getModelResource(RedStarExplosion animatable) {
        return Companions.of("geo/red_star_explosion.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RedStarExplosion animatable) {
        int frames = 14;
        int perTick = 1;

        int frameIndex = (animatable.tickCount / perTick) % frames;
        return Companions.of(String.format("textures/entity/red_star_explosion_center_%d.png", frameIndex));
    }

    @Override
    public ResourceLocation getAnimationResource(RedStarExplosion animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}
