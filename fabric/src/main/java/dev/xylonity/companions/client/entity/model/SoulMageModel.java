package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SoulMageModel extends GeoModel<SoulMageEntity> {

    @Override
    public ResourceLocation getModelResource(SoulMageEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/soul_mage.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SoulMageEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/soul_mage.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SoulMageEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/soul_mage.animation.json");
    }

    @Override
    public void setCustomAnimations(SoulMageEntity animatable, long instanceId, AnimationState<SoulMageEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !animatable.isAttacking()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY((entityData.netHeadYaw() * 0.5f) * Mth.DEG_TO_RAD);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }

}