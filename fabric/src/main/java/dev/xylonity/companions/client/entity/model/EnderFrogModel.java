package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.summon.EnderFrogEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EnderFrogModel extends GeoModel<EnderFrogEntity> {

    @Override
    public ResourceLocation getModelResource(EnderFrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/ender_frog.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EnderFrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/ender_frog.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EnderFrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/ender_frog.animation.json");
    }

    @Override
    public void setCustomAnimations(EnderFrogEntity animatable, long instanceId, AnimationState<EnderFrogEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY((entityData.netHeadYaw() * 0.5f) * Mth.DEG_TO_RAD);
        }
    }

}