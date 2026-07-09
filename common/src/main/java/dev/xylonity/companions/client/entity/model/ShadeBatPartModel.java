package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.companion.ShadeBatPartEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShadeBatPartModel extends GeoModel<ShadeBatPartEntity> {

    @Override
    public ResourceLocation getModelResource(ShadeBatPartEntity animatable) {
        return Companions.of("geo/shade_bat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShadeBatPartEntity animatable) {
        return Companions.of("textures/entity/shade_bat.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShadeBatPartEntity animatable) {
        return Companions.of("animations/shade_bat.animation.json");
    }

}
