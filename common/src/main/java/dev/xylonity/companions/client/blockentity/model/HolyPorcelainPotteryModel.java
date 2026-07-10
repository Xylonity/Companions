package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.HolyPorcelainPotteryBlockEntity;
import dev.xylonity.companions.common.blockentity.PorcelainPotteryBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HolyPorcelainPotteryModel extends GeoModel<HolyPorcelainPotteryBlockEntity> {

    @Override
    public ResourceLocation getModelResource(HolyPorcelainPotteryBlockEntity animatable) {
        return Companions.of("geo/holy_porcelain_pottery.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HolyPorcelainPotteryBlockEntity animatable) {
        return Companions.of("textures/block/holy_porcelain_pottery.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HolyPorcelainPotteryBlockEntity animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}