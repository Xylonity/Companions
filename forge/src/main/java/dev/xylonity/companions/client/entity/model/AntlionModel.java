package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AntlionModel extends GeoModel<AntlionEntity> {

    @Override
    public ResourceLocation getModelResource(AntlionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/antlion.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AntlionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/antlion.png");
    }

    @Override
    public ResourceLocation getAnimationResource(AntlionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/antlion.animation.json");
    }

}