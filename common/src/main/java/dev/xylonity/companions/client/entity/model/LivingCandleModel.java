package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.summon.LivingCandleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LivingCandleModel extends GeoModel<LivingCandleEntity> {

    @Override
    public ResourceLocation getModelResource(LivingCandleEntity animatable) {
        return Companions.of("geo/living_candle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LivingCandleEntity animatable) {
        return Companions.of("textures/entity/living_candle" + animatable.tickCount / 3 % 5 + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(LivingCandleEntity animatable) {
        return Companions.of("animations/living_candle.animation.json");
    }

}