package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.RecallPlatformBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RecallPlatformModel extends GeoModel<RecallPlatformBlockEntity> {

    @Override
    public ResourceLocation getModelResource(RecallPlatformBlockEntity animatable) {
        return Companions.of("geo/recall_platform_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RecallPlatformBlockEntity animatable) {
        if (animatable.isActive()) {
            return Companions.of("textures/block/recall_platform_on_block.png");
        }

        return Companions.of("textures/block/recall_platform_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RecallPlatformBlockEntity animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}