package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.GoldenAllayEntity;
import dev.xylonity.companions.common.entity.custom.SoulMageEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SoulMageModel extends GeoModel<SoulMageEntity> {

    @Override
    public ResourceLocation getModelResource(SoulMageEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/soul_mage.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SoulMageEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/soul_mage.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SoulMageEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/soul_mage.animation.json");
    }

}