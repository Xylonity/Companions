package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.MinionEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MinionModel extends GeoModel<MinionEntity> {

    @Override
    public ResourceLocation getModelResource(MinionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/" + animatable.getVariant() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MinionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/" + animatable.getVariant() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(MinionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/" + animatable.getVariant() + ".animation.json");
    }

    @Override
    public void setCustomAnimations(MinionEntity animatable, long instanceId, AnimationState<MinionEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && animatable.getAttackType() == 0) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY((entityData.netHeadYaw() * 0.5f) * Mth.DEG_TO_RAD);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }

}