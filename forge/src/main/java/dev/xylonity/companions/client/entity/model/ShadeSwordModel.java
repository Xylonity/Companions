package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.ShadeSwordEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadeSwordModel extends GeoModel<ShadeSwordEntity> {

    @Override
    public ResourceLocation getModelResource(ShadeSwordEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/shade_sword.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShadeSwordEntity animatable) {
        if (animatable.isBlood()) {
            return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/shade_sword_blood.png");
        }

        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/shade_sword.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShadeSwordEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/shade_sword.animation.json");
    }

}