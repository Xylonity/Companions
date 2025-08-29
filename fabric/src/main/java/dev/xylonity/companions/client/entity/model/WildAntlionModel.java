package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.hostile.WildAntlionEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class WildAntlionModel extends GeoModel<WildAntlionEntity> {

    @Override
    public ResourceLocation getModelResource(WildAntlionEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "geo/wild_antlion.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WildAntlionEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/wild_antlion.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WildAntlionEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "animations/wild_antlion.animation.json");
    }

    @Override
    public void setCustomAnimations(WildAntlionEntity animatable, long instanceId, AnimationState<WildAntlionEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && animatable.getAttackType() == 0 && entityData != null) {
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY((entityData.netHeadYaw() * 0.5f) * Mth.DEG_TO_RAD);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }

}