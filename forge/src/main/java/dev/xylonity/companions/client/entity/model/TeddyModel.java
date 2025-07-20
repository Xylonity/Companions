package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.TeddyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TeddyModel extends GeoModel<TeddyEntity> {

    private String prefix(TeddyEntity animatable) {
        return animatable.getPhase() == 1 ? "" : "mutated_";
    }

    @Override
    public ResourceLocation getModelResource(TeddyEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "geo/" + prefix(animatable) +"teddy.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TeddyEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/" + prefix(animatable) + "teddy.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TeddyEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "animations/" + prefix(animatable) + "teddy.animation.json");
    }

}