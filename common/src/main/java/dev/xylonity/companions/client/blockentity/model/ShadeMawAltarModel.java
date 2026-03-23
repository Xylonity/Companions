package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.ShadeMawAltarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadeMawAltarModel extends GeoModel<ShadeMawAltarBlockEntity> {

    @Override
    public ResourceLocation getModelResource(ShadeMawAltarBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/shade_maw_altar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShadeMawAltarBlockEntity animatable) {
        if (animatable.isBloodUpgradeActive()) {
            return new ResourceLocation(Companions.MOD_ID, "textures/block/shade_maw_altar_blood.png");
        } else if (animatable.getCharges() == 0) {
            return new ResourceLocation(Companions.MOD_ID, "textures/block/shade_maw_altar_off.png");
        }

        return new ResourceLocation(Companions.MOD_ID, "textures/block/shade_maw_altar.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShadeMawAltarBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/shade_maw_altar.animation.json");
    }

}