package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.hostile.HostileImpEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HostileImpModel extends GeoModel<HostileImpEntity> {

    @Override
    public ResourceLocation getModelResource(HostileImpEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "geo/hostile_imp.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HostileImpEntity animatable) {
        if (animatable.isAngry()) {
            return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/imp.png");
        }

        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/imp_hostile1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HostileImpEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "animations/imp.animation.json");
    }

}