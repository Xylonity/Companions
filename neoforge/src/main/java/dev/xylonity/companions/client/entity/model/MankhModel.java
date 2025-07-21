package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.MankhEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MankhModel extends GeoModel<MankhEntity> {

    @Override
    public ResourceLocation getModelResource(MankhEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "geo/mankh.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MankhEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/mankh.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MankhEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "animations/mankh.animation.json");
    }

}