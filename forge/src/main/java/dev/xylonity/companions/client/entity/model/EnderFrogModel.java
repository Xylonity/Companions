package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.summon.EnderFrogEntity;
import dev.xylonity.companions.common.entity.summon.FireworkToadEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EnderFrogModel extends GeoModel<EnderFrogEntity> {

    @Override
    public ResourceLocation getModelResource(EnderFrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/ender_frog.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EnderFrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/ender_frog.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EnderFrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/generic.animation.json");
    }

}