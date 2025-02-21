package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import dev.xylonity.companions.common.entity.custom.HostileImpEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HostileImpModel extends GeoModel<HostileImpEntity> {

    @Override
    public ResourceLocation getModelResource(HostileImpEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/imp.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HostileImpEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/imp_hostile1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HostileImpEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/imp.animation.json");
    }

}