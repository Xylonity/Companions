package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.PuppetGloveEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PuppetGloveModel extends GeoModel<PuppetGloveEntity> {

    @Override
    public ResourceLocation getModelResource(PuppetGloveEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/puppet_glove.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PuppetGloveEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/puppet_glove.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PuppetGloveEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/puppet_glove.animation.json");
    }

}