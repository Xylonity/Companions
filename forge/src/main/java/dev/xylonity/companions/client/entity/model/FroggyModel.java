package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.FroggyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FroggyModel extends GeoModel<FroggyEntity> {

    @Override
    public ResourceLocation getModelResource(FroggyEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/froggy.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FroggyEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/froggy.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FroggyEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/froggy.animation.json");
    }

}