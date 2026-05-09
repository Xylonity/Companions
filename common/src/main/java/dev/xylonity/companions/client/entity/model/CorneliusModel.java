package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.companion.CorneliusEntity;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CorneliusModel extends GeoModel<CorneliusEntity> {

    @Override
    public ResourceLocation getModelResource(CorneliusEntity animatable) {
        return Companions.of("geo/cornelius.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CorneliusEntity animatable) {
        if (Util.matchesReskinName(animatable, CompanionsConfig.CORNELIUS_RESKIN_NAMES)) {
            return Companions.of("textures/entity/cornelius_reskin.png");
        }

        return Companions.of("textures/entity/cornelius.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CorneliusEntity animatable) {
        return Companions.of("animations/cornelius.animation.json");
    }

}