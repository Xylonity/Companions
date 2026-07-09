package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.ShadeBatAltarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadeBatAltarModel extends GeoModel<ShadeBatAltarBlockEntity> {

    @Override
    public ResourceLocation getModelResource(ShadeBatAltarBlockEntity animatable) {
        return Companions.of("geo/shade_bat_altar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShadeBatAltarBlockEntity animatable) {
        if (animatable.isBloodUpgradeActive()) {
            return Companions.of("textures/block/shade_bat_altar_blood.png");
        }
        else if (animatable.getCharges() == 0) {
            return Companions.of("textures/block/shade_bat_altar_off.png");
        }

        return Companions.of("textures/block/shade_bat_altar.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShadeBatAltarBlockEntity animatable) {
        return Companions.of("animations/shade_bat_altar.animation.json");
    }

}