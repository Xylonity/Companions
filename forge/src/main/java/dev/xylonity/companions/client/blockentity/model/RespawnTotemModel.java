package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.CroissantEggBlockEntity;
import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RespawnTotemModel extends GeoModel<RespawnTotemBlockEntity> {

    @Override
    public ResourceLocation getModelResource(RespawnTotemBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/froggy.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RespawnTotemBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/froggy.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RespawnTotemBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}