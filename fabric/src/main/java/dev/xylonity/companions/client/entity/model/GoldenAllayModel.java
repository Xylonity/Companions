package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.GoldenAllayEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GoldenAllayModel extends GeoModel<GoldenAllayEntity> {

    @Override
    public ResourceLocation getModelResource(GoldenAllayEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/golden_allay.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GoldenAllayEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/golden_allay.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GoldenAllayEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/golden_allay.animation.json");
    }

}