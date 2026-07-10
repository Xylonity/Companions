package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.companion.MankhEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MankhModel extends GeoModel<MankhEntity> {

    @Override
    public ResourceLocation getModelResource(MankhEntity animatable) {
        return Companions.of("geo/mankh.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MankhEntity animatable) {
        return Companions.of("textures/entity/mankh.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MankhEntity animatable) {
        return Companions.of("animations/mankh.animation.json");
    }

    @Override
    public void setCustomAnimations(MankhEntity animatable, long instanceId, AnimationState<MankhEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && animatable.getAttackType() == 0) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY((entityData.netHeadYaw() * 0.5f) * Mth.DEG_TO_RAD);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }

}