package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.PuppetEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PuppetModel extends GeoModel<PuppetEntity> {

    @Override
    public ResourceLocation getModelResource(PuppetEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/puppet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PuppetEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/puppet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PuppetEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/puppet.animation.json");
    }

}