package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.CroissantEggBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CroissantEggModel extends GeoModel<CroissantEggBlockEntity> {

    @Override
    public ResourceLocation getModelResource(CroissantEggBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/croissant_egg_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CroissantEggBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/block/croissant_egg_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CroissantEggBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/croissant_egg_block.animation.json");
    }

}