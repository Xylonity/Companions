package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.AntlionEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class AntlionModel extends GeoModel<AntlionEntity> {

    @Override
    public ResourceLocation getModelResource(AntlionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/antlion" + prefix(animatable) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AntlionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/antlion" + prefix(animatable) + ((animatable.hasFur() && animatable.getVariant() == 0) ? "_hair" : "") + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(AntlionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/antlion" + prefix(animatable) + ".animation.json");
    }

    private String prefix(AntlionEntity animatable) {
        return switch (animatable.getVariant()) {
            case 0 -> "_base";
            case 1 -> "_pupa";
            case 2 -> "_adult";
            default -> "_soldier";
        };
    }

    @Override
    public void setCustomAnimations(AntlionEntity animatable, long instanceId, AnimationState<AntlionEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null && animatable.getAttackType() == 0) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY((entityData.netHeadYaw() * 0.5f) * Mth.DEG_TO_RAD);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }

}