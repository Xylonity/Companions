package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.summon.BubbleFrogEntity;
import dev.xylonity.companions.common.entity.summon.FireworkToadEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BubbleFrogModel extends GeoModel<BubbleFrogEntity> {

    @Override
    public ResourceLocation getModelResource(BubbleFrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/bubble_frog.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BubbleFrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/bubble_frog.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BubbleFrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/bubble_frog.animation.json");
    }

}