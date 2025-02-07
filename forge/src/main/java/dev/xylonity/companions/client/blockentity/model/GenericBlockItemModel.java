package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.item.blockitem.GenericBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericBlockItemModel extends GeoModel<GenericBlockItem> {

    @Override
    public ResourceLocation getModelResource(GenericBlockItem animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/tesla_receiver_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GenericBlockItem animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/dinamo.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GenericBlockItem animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/tamed_illager_golem.animation.json");
    }

}
