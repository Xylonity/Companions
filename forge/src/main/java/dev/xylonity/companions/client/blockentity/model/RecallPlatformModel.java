package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.RecallPlatformBlockEntity;
import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RecallPlatformModel extends GeoModel<RecallPlatformBlockEntity> {

    @Override
    public ResourceLocation getModelResource(RecallPlatformBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/recall_platform_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RecallPlatformBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/block/recall_platform_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RecallPlatformBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}