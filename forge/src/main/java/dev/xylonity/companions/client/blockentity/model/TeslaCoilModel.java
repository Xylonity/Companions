package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TeslaCoilModel extends GeoModel<TeslaCoilBlockEntity> {

    @Override
    public ResourceLocation getModelResource(TeslaCoilBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/tesla_coil_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TeslaCoilBlockEntity animatable) {
        TeslaConnectionManager manager = TeslaConnectionManager.getInstance();
        TeslaConnectionManager.ConnectionNode node = animatable.asConnectionNode();
        if (animatable.isActive() && (!manager.getOutgoing(node).isEmpty() || !manager.getIncoming(node).isEmpty())) return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/dinamo_charge.png");

        return new ResourceLocation(Companions.MOD_ID, "textures/entity/dinamo.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TeslaCoilBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}