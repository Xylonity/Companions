package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.summon.EmberPoleEntity;
import dev.xylonity.companions.common.entity.summon.FireworkToadEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EmberPoleModel extends GeoModel<EmberPoleEntity> {

    @Override
    public ResourceLocation getModelResource(EmberPoleEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/ember_pole.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EmberPoleEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/ember_pole.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EmberPoleEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/generic.animation.json");
    }

}