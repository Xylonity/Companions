package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SoulMageModel extends GeoModel<SoulMageEntity> {

    @Override
    public ResourceLocation getModelResource(SoulMageEntity animatable) {
        return Companions.of("geo/soul_mage.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SoulMageEntity animatable) {
        if (Util.matchesReskinName(animatable, CompanionsConfig.SOUL_MAGE_RESKIN_NAMES)) {
            return Companions.of("textures/entity/soul_mage_reskin.png");
        }

        return Companions.of("textures/entity/soul_mage.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SoulMageEntity animatable) {
        return Companions.of("animations/soul_mage.animation.json");
    }

    @Override
    public void setCustomAnimations(SoulMageEntity animatable, long instanceId, AnimationState<SoulMageEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && !animatable.isAttacking()) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY((entityData.netHeadYaw() * 0.5f) * Mth.DEG_TO_RAD);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }

}