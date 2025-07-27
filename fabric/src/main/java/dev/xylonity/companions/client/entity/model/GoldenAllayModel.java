package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.GoldenAllayEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class GoldenAllayModel extends GeoModel<GoldenAllayEntity> {

    @Override
    public ResourceLocation getModelResource(GoldenAllayEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/golden_allay.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GoldenAllayEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/golden_allay.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GoldenAllayEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/golden_allay.animation.json");
    }

    @Override
    public void setCustomAnimations(GoldenAllayEntity animatable, long instanceId, AnimationState<GoldenAllayEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY((entityData.netHeadYaw() * 0.5f) * Mth.DEG_TO_RAD);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }

}