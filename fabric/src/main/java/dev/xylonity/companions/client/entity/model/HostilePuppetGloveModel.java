package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.hostile.HostilePuppetGloveEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HostilePuppetGloveModel extends GeoModel<HostilePuppetGloveEntity> {

    @Override
    public ResourceLocation getModelResource(HostilePuppetGloveEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/hostile_puppet_glove.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HostilePuppetGloveEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/puppet_glove.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HostilePuppetGloveEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/hostile_puppet_glove.animation.json");
    }

}