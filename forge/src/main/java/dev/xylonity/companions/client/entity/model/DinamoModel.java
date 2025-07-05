package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.DinamoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DinamoModel extends GeoModel<DinamoEntity> {

    @Override
    public ResourceLocation getModelResource(DinamoEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/illager_golem.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DinamoEntity animatable) {
        if (animatable.getMainAction() == 0 && animatable.isActive()) {
            return new ResourceLocation(Companions.MOD_ID, "textures/entity/dinamo_charge.png");
        } else if (animatable.getMainAction() != 0 && animatable.isActiveForAttack() && !animatable.entitiesToAttack.isEmpty()) {
            return new ResourceLocation(Companions.MOD_ID, "textures/entity/dinamo_charge.png");
        }

        return new ResourceLocation(Companions.MOD_ID, "textures/entity/dinamo.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DinamoEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/illager_golem.animation.json");
    }

}