package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.CroissantDragonEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CroissantDragonModel extends GeoModel<CroissantDragonEntity> {

    private final String TPATH = "textures/entity/croissant_dragon_";

    @Override
    public ResourceLocation getModelResource(CroissantDragonEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/croissant_dragon.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CroissantDragonEntity animatable) {
        if (animatable.isAttacking()) {
            return new ResourceLocation(CompanionsCommon.MOD_ID, TPATH + animatable.getArmorName() + "_attack.png");
        } else {
            return new ResourceLocation(CompanionsCommon.MOD_ID, TPATH + animatable.getArmorName() + ".png");
        }
    }

    @Override
    public ResourceLocation getAnimationResource(CroissantDragonEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/croissant_dragon.animation.json");
    }

}