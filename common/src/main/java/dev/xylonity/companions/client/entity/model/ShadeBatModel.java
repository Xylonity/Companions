package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.companion.ShadeBatEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadeBatModel extends GeoModel<ShadeBatEntity> {

    @Override
    public ResourceLocation getModelResource(ShadeBatEntity animatable) {
        if (animatable.isBlood()) {
            return Companions.of("geo/shade_bat_blood.geo.json");
        }

        return Companions.of("geo/shade_bat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShadeBatEntity animatable) {
        if (animatable.isBlood()) {
            return Companions.of("textures/entity/shade_bat_blood.png");
        }

        return Companions.of("textures/entity/shade_bat.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShadeBatEntity animatable) {
        if (animatable.isBlood()) {
            return Companions.of("animations/shade_bat_blood.animation.json");
        }

        return Companions.of("animations/shade_bat.animation.json");
    }

}
