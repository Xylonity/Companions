package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.companion.ShadeMawEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class ShadeMawModel extends GeoModel<ShadeMawEntity> {

    @Override
    public ResourceLocation getModelResource(ShadeMawEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/shade_maw.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShadeMawEntity animatable) {
        if (animatable.isBlood()) {
            return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/shade_maw_blood.png");
        }

        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/shade_maw.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShadeMawEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/shade_maw.animation.json");
    }

    @Override
    public void setCustomAnimations(ShadeMawEntity anim, long instanceId, AnimationState<ShadeMawEntity> state) {
        super.setCustomAnimations(anim, instanceId, state);

        CoreGeoBone entity = getAnimationProcessor().getBone("entity");
        if (entity != null && anim.isInAnyFluid()) {
            float pitch = Mth.lerp(state.getPartialTick(), anim.xRotO, anim.getXRot());
            pitch = Mth.clamp(pitch, -90f, 90f);

            entity.setRotX(-pitch * Mth.DEG_TO_RAD);
        }

    }

}
