package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.hostile.HostilePuppetGloveEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HostilePuppetGloveModel extends GeoModel<HostilePuppetGloveEntity> {

    @Override
    public ResourceLocation getModelResource(HostilePuppetGloveEntity animatable) {
        return Companions.of("geo/hostile_puppet_glove.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HostilePuppetGloveEntity animatable) {
        return Companions.of("textures/entity/puppet_glove.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HostilePuppetGloveEntity animatable) {
        return Companions.of("animations/hostile_puppet_glove.animation.json");
    }

}