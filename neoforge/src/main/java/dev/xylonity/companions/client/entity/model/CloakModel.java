package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.CloakEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CloakModel extends GeoModel<CloakEntity> {

    @Override
    public ResourceLocation getModelResource(CloakEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "geo/cloak.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CloakEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/cloak_" + ((animatable.tickCount / 2) % 5) + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(CloakEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "animations/cloak.animation.json");
    }

    @Override
    public void setCustomAnimations(CloakEntity animatable, long instanceId, AnimationState<CloakEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && animatable.getAttackType() == 0 && entityData != null) {
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY((entityData.netHeadYaw() * 0.5f) * Mth.DEG_TO_RAD);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }

}