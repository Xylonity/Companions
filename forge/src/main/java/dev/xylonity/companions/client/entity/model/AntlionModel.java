package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import dev.xylonity.companions.common.entity.hostile.WildAntlionEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AntlionModel extends GeoModel<AntlionEntity> {

    @Override
    public ResourceLocation getModelResource(AntlionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/antlion" + prefix(animatable) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AntlionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/antlion" + prefix(animatable) + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(AntlionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/antlion" + prefix(animatable) + ".animation.json");
    }

    private String prefix(AntlionEntity animatable) {
        return switch (animatable.getVariant()) {
            case 0 -> "_base";
            case 1 -> "_pupa";
            case 2 -> "_adult";
            default -> "_soldier";
        };
    }

}