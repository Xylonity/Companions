package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.EmptyPuppetBlockEntity;
import dev.xylonity.companions.common.blockentity.PorcelainPotteryBlockEntity;
import dev.xylonity.companions.common.item.PorcelainPottery;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PorcelainPotteryModel extends GeoModel<PorcelainPotteryBlockEntity> {

    @Override
    public ResourceLocation getModelResource(PorcelainPotteryBlockEntity animatable) {
        return Companions.of("geo/porcelain_pottery.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PorcelainPotteryBlockEntity animatable) {
        return Companions.of("textures/block/porcelain_pottery.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PorcelainPotteryBlockEntity animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}