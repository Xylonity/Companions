package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import dev.xylonity.companions.common.entity.custom.HostilePuppetGlove;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HostilePuppetGloveModel extends GeoModel<HostilePuppetGlove> {

    @Override
    public ResourceLocation getModelResource(HostilePuppetGlove animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/hostile_puppet_glove.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HostilePuppetGlove animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/puppet_glove.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HostilePuppetGlove animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/hostile_puppet_glove.animation.json");
    }

}