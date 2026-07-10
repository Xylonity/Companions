package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.summon.BubbleFrogEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BubbleFrogModel extends GeoModel<BubbleFrogEntity> {

    @Override
    public ResourceLocation getModelResource(BubbleFrogEntity animatable) {
        return Companions.of("geo/bubble_frog.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BubbleFrogEntity animatable) {
        return Companions.of("textures/entity/bubble_frog.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BubbleFrogEntity animatable) {
        return Companions.of("animations/bubble_frog.animation.json");
    }

}