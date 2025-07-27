package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.TeddyEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TeddyModel extends GeoModel<TeddyEntity> {

    private String prefix(TeddyEntity animatable) {
        return animatable.getPhase() == 1 ? "" : "mutated_";
    }

    @Override
    public ResourceLocation getModelResource(TeddyEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/" + prefix(animatable) +"teddy.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TeddyEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/" + prefix(animatable) + "teddy.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TeddyEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/" + prefix(animatable) + "teddy.animation.json");
    }

    @Override
    public void setCustomAnimations(TeddyEntity animatable, long instanceId, AnimationState<TeddyEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && animatable.getAttackType() == 0) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY((entityData.netHeadYaw() * 0.5f) * Mth.DEG_TO_RAD);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }

}