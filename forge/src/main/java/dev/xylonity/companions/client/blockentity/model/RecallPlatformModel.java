package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.CompanionsForge;
import dev.xylonity.companions.common.blockentity.RecallPlatformBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RecallPlatformModel extends GeoModel<RecallPlatformBlockEntity> {

    @Override
    public ResourceLocation getModelResource(RecallPlatformBlockEntity animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "geo/recall_platform_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RecallPlatformBlockEntity animatable) {
        if (animatable.isActive()) {
            return new ResourceLocation(CompanionsForge.MOD_ID, "textures/block/recall_platform_on_block.png");
        }

        return new ResourceLocation(CompanionsForge.MOD_ID, "textures/block/recall_platform_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RecallPlatformBlockEntity animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "animations/generic.animation.json");
    }

}