package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.HostilePuppetGlove;
import dev.xylonity.companions.common.entity.custom.PuppetGlove;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PuppetGloveModel extends GeoModel<PuppetGlove> {

    @Override
    public ResourceLocation getModelResource(PuppetGlove animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/puppet_glove.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PuppetGlove animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/puppet_glove.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PuppetGlove animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/puppet_glove.animation.json");
    }

}