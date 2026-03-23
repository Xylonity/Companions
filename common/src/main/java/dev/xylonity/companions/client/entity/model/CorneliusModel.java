package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CorneliusModel extends GeoModel<CorneliusEntity> {

    @Override
    public ResourceLocation getModelResource(CorneliusEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/cornelius.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CorneliusEntity animatable) {
        if (animatable.hasCustomName()) {
            return new ResourceLocation(Companions.MOD_ID, "textures/entity/cornelius_reskin.png");
        }

        return new ResourceLocation(Companions.MOD_ID, "textures/entity/cornelius.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CorneliusEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/cornelius.animation.json");
    }

}