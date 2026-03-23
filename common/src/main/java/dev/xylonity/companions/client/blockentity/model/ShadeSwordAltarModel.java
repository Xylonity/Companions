package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.ShadeSwordAltarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadeSwordAltarModel extends GeoModel<ShadeSwordAltarBlockEntity> {

    @Override
    public ResourceLocation getModelResource(ShadeSwordAltarBlockEntity animatable) {
        return Companions.of("geo/shade_sword_altar.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShadeSwordAltarBlockEntity animatable) {
        if (animatable.isBloodUpgradeActive()) {
            return Companions.of("textures/block/shade_sword_altar_blood.png");
        } else if (animatable.getCharges() == 0) {
            return Companions.of("textures/block/shade_sword_altar_off.png");
        }

        return Companions.of("textures/block/shade_sword_altar.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShadeSwordAltarBlockEntity animatable) {
        return Companions.of("animations/shade_sword_altar.animation.json");
    }

}