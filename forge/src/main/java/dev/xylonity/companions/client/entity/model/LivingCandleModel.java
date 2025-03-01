package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.GoldenAllayEntity;
import dev.xylonity.companions.common.entity.custom.LivingCandleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LivingCandleModel extends GeoModel<LivingCandleEntity> {

    @Override
    public ResourceLocation getModelResource(LivingCandleEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/living_candle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LivingCandleEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/living_candle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LivingCandleEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/living_candle.animation.json");
    }

}