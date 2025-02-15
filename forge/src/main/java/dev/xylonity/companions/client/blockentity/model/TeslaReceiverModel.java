package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.blockentity.TeslaReceiverBlockEntity;
import dev.xylonity.companions.common.entity.ai.illagergolem.TeslaConnectionManager;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TeslaReceiverModel extends GeoModel<TeslaReceiverBlockEntity> {

    @Override
    public ResourceLocation getModelResource(TeslaReceiverBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/tesla_receiver_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TeslaReceiverBlockEntity animatable) {
        TeslaConnectionManager manager = TeslaConnectionManager.getInstance();
        TeslaConnectionManager.ConnectionNode node = animatable.asConnectionNode();
        if (animatable.isActive() && (!manager.getOutgoing(node).isEmpty() || !manager.getIncoming(node).isEmpty())) return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/dinamo_charge.png");

        return new ResourceLocation(Companions.MOD_ID, "textures/entity/dinamo.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TeslaReceiverBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/tamed_illager_golem.animation.json");
    }

}