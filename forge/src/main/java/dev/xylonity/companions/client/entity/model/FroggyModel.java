package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.FroggyEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FroggyModel extends GeoModel<FroggyEntity> {

    @Override
    public ResourceLocation getModelResource(FroggyEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/froggy.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FroggyEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/froggy.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FroggyEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/froggy.animation.json");
    }

}