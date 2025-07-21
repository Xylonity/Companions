package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.summon.LivingCandleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LivingCandleModel extends GeoModel<LivingCandleEntity> {

    @Override
    public ResourceLocation getModelResource(LivingCandleEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "geo/living_candle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LivingCandleEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/living_candle" + animatable.tickCount / 3 % 5 + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(LivingCandleEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "animations/living_candle.animation.json");
    }

}