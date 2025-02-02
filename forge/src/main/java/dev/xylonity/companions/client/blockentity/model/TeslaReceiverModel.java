package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.TeslaReceiverBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TeslaReceiverModel extends GeoModel<TeslaReceiverBlockEntity> {

    @Override
    public ResourceLocation getModelResource(TeslaReceiverBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/tesla_receiver_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TeslaReceiverBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/tamed_illager_golem.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TeslaReceiverBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/tamed_illager_golem.animation.json");
    }

}