package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.ShadeMawEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadeMawModel extends GeoModel<ShadeMawEntity> {

    @Override
    public ResourceLocation getModelResource(ShadeMawEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "geo/shade_maw.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShadeMawEntity animatable) {
        if (animatable.isBlood()) {
            return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/shade_maw_blood.png");
        }

        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/shade_maw.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShadeMawEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "animations/shade_maw.animation.json");
    }

}