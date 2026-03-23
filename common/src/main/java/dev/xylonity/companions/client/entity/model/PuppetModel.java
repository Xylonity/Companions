package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.companion.PuppetEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PuppetModel extends GeoModel<PuppetEntity> {

    @Override
    public ResourceLocation getModelResource(PuppetEntity animatable) {
        return Companions.of("geo/puppet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PuppetEntity animatable) {
        return Companions.of("textures/entity/puppet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PuppetEntity animatable) {
        return Companions.of("animations/puppet.animation.json");
    }

}