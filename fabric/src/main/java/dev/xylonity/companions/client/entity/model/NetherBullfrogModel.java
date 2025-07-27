package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.summon.NetherBullfrogEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class NetherBullfrogModel extends GeoModel<NetherBullfrogEntity> {

    @Override
    public ResourceLocation getModelResource(NetherBullfrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/nether_bullfrog.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(NetherBullfrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/nether_bullfrog.png");
    }

    @Override
    public ResourceLocation getAnimationResource(NetherBullfrogEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/nether_bullfrog.animation.json");
    }

    @Override
    public void setCustomAnimations(NetherBullfrogEntity animatable, long instanceId, AnimationState<NetherBullfrogEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && animatable.getAttackType() == 0) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY((entityData.netHeadYaw() * 0.5f) * Mth.DEG_TO_RAD);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }

}