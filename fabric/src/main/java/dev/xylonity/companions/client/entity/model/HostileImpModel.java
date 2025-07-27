package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.hostile.HostileImpEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HostileImpModel extends GeoModel<HostileImpEntity> {

    @Override
    public ResourceLocation getModelResource(HostileImpEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/hostile_imp.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HostileImpEntity animatable) {
        if (animatable.isAngry()) {
            return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/imp.png");
        }

        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/imp_hostile1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HostileImpEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/imp.animation.json");
    }

    @Override
    public void setCustomAnimations(HostileImpEntity animatable, long instanceId, AnimationState<HostileImpEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && animatable.getAttackType() == 0) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY((entityData.netHeadYaw() * 0.5f) * Mth.DEG_TO_RAD);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }

}