package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.FloorCakeCreamProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FloorCakeCreamModel extends GeoModel<FloorCakeCreamProjectile> {

    @Override
    public ResourceLocation getModelResource(FloorCakeCreamProjectile animatable) {
        return Companions.of("geo/floor_cake_cream.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FloorCakeCreamProjectile animatable) {
        if (animatable.getArmorName().equals("chocolate")) {
            return Companions.of("textures/entity/floor_cake_cream_chocolate.png");
        } else if (animatable.getArmorName().equals("strawberry")) {
            return Companions.of("textures/entity/floor_cake_cream_strawberry.png");
        }

        return Companions.of("textures/entity/floor_cake_cream.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FloorCakeCreamProjectile animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}
