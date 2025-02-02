package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.IllagerGolemEntity;
import dev.xylonity.companions.common.entity.custom.TamedIllagerGolemEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TamedIllagerGolemModel extends GeoModel<TamedIllagerGolemEntity> {

    @Override
    public ResourceLocation getModelResource(TamedIllagerGolemEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/illager_golem.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TamedIllagerGolemEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/illager_golem.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TamedIllagerGolemEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/illager_golem.animation.json");
    }

}