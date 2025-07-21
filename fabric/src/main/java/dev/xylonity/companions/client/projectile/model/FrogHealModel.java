package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.FrogHealProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FrogHealModel extends GeoModel<FrogHealProjectile> {

    @Override
    public ResourceLocation getModelResource(FrogHealProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/frog_heal.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FrogHealProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/entity/frog_heal.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FrogHealProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "animations/frog_heal.animation.json");
    }

}
