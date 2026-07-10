package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.BlueOrbProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlueOrbModel extends GeoModel<BlueOrbProjectile> {

    @Override
    public ResourceLocation getModelResource(BlueOrbProjectile animatable) {
        return Companions.of("geo/blue_orb.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlueOrbProjectile animatable) {
        return Companions.of("textures/entity/blue_orb.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlueOrbProjectile animatable) {
        return Companions.of("animations/star.animation.json");
    }

}
