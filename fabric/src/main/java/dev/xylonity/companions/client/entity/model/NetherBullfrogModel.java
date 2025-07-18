package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.summon.NetherBullfrogEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NetherBullfrogModel extends GeoModel<NetherBullfrogEntity> {

    @Override
    public ResourceLocation getModelResource(NetherBullfrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/nether_bullfrog.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(NetherBullfrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/nether_bullfrog.png");
    }

    @Override
    public ResourceLocation getAnimationResource(NetherBullfrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/nether_bullfrog.animation.json");
    }

}