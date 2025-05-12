package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SacredPontiffModel extends GeoModel<SacredPontiffEntity> {

    @Override
    public ResourceLocation getModelResource(SacredPontiffEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/sacred_pontiff.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SacredPontiffEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/sacred_pontiff.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SacredPontiffEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/sacred_pontiff.animation.json");
    }

}