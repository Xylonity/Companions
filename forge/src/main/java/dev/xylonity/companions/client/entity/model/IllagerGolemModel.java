package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import dev.xylonity.companions.common.entity.custom.IllagerGolemEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IllagerGolemModel extends GeoModel<IllagerGolemEntity> {

    @Override
    public ResourceLocation getModelResource(IllagerGolemEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/illager_golem.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IllagerGolemEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/illager_golem.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IllagerGolemEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/illager_golem.animation.json");
    }

}