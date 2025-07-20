package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.FrogBonanzaBlockEntity;
import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FrogBonanzaModel extends GeoModel<FrogBonanzaBlockEntity> {

    @Override
    public ResourceLocation getModelResource(FrogBonanzaBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/frog_bonanza_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FrogBonanzaBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/block/frog_bonanza_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FrogBonanzaBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/frog_bonanza_block.animation.json");
    }

}