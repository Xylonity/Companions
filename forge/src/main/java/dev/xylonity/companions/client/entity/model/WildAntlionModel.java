package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.hostile.WildAntlionEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WildAntlionModel extends GeoModel<WildAntlionEntity> {

    @Override
    public ResourceLocation getModelResource(WildAntlionEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "geo/wild_antlion.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WildAntlionEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/wild_antlion.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WildAntlionEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "animations/wild_antlion.animation.json");
    }

}