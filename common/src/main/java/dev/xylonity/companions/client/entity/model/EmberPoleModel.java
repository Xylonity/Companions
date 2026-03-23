package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.summon.EmberPoleEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EmberPoleModel extends GeoModel<EmberPoleEntity> {

    @Override
    public ResourceLocation getModelResource(EmberPoleEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/ember_pole.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EmberPoleEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/ember_pole.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EmberPoleEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/ember_pole.animation.json");
    }

}