package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.CloakEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CloakModel extends GeoModel<CloakEntity> {

    @Override
    public ResourceLocation getModelResource(CloakEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/cloak.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CloakEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/cloak.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CloakEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/cloak.animation.json");
    }

}