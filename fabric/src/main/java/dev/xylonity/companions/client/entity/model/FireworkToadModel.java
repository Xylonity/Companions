package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.summon.FireworkToadEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FireworkToadModel extends GeoModel<FireworkToadEntity> {

    @Override
    public ResourceLocation getModelResource(FireworkToadEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/firework_toad.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FireworkToadEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/firework_toad.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FireworkToadEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/firework_toad.animation.json");
    }

}