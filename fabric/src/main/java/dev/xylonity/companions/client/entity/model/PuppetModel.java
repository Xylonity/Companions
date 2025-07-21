package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.PuppetEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PuppetModel extends GeoModel<PuppetEntity> {

    @Override
    public ResourceLocation getModelResource(PuppetEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "geo/puppet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PuppetEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/puppet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PuppetEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "animations/puppet.animation.json");
    }

}