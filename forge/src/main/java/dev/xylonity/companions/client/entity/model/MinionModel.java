package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.MinionEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MinionModel extends GeoModel<MinionEntity> {

    @Override
    public ResourceLocation getModelResource(MinionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/" + animatable.getVariant() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MinionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/" + animatable.getVariant() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(MinionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/" + animatable.getVariant() + ".animation.json");
    }

}