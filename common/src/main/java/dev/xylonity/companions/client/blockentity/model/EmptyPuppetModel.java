package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.EmptyPuppetBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EmptyPuppetModel extends GeoModel<EmptyPuppetBlockEntity> {

    @Override
    public ResourceLocation getModelResource(EmptyPuppetBlockEntity animatable) {
        return Companions.of("geo/empty_puppet_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EmptyPuppetBlockEntity animatable) {
        return Companions.of("textures/block/empty_puppet_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EmptyPuppetBlockEntity animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}