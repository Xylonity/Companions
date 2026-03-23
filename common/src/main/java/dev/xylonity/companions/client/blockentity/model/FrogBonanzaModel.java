package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.FrogBonanzaBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FrogBonanzaModel extends GeoModel<FrogBonanzaBlockEntity> {

    @Override
    public ResourceLocation getModelResource(FrogBonanzaBlockEntity animatable) {
        return Companions.of("geo/frog_bonanza_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FrogBonanzaBlockEntity animatable) {
        return Companions.of("textures/block/frog_bonanza_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FrogBonanzaBlockEntity animatable) {
        return Companions.of("animations/frog_bonanza_block.animation.json");
    }

}