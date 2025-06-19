package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.CorneliusEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CorneliusModel extends GeoModel<CorneliusEntity> {

    @Override
    public ResourceLocation getModelResource(CorneliusEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/cornelius.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CorneliusEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/cornelius.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CorneliusEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/cornelius.animation.json");
    }

}