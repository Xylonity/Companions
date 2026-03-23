package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.CompanionsForge;
import dev.xylonity.companions.common.entity.projectile.FloorCakeCreamProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FloorCakeCreamModel extends GeoModel<FloorCakeCreamProjectile> {

    @Override
    public ResourceLocation getModelResource(FloorCakeCreamProjectile animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "geo/floor_cake_cream.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FloorCakeCreamProjectile animatable) {
        if (animatable.getArmorName().equals("chocolate")) {
            return new ResourceLocation(CompanionsForge.MOD_ID, "textures/entity/floor_cake_cream_chocolate.png");
        } else if (animatable.getArmorName().equals("strawberry")) {
            return new ResourceLocation(CompanionsForge.MOD_ID, "textures/entity/floor_cake_cream_strawberry.png");
        }

        return new ResourceLocation(CompanionsForge.MOD_ID, "textures/entity/floor_cake_cream.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FloorCakeCreamProjectile animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "animations/generic.animation.json");
    }

}
