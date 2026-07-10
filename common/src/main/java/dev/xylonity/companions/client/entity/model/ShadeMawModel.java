package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.companion.ShadeMawEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class ShadeMawModel extends GeoModel<ShadeMawEntity> {

    @Override
    public ResourceLocation getModelResource(ShadeMawEntity animatable) {
        return Companions.of("geo/shade_maw.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ShadeMawEntity animatable) {
        if (animatable.isBlood()) {
            return Companions.of("textures/entity/shade_maw_blood.png");
        }

        return Companions.of("textures/entity/shade_maw.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ShadeMawEntity animatable) {
        return Companions.of("animations/shade_maw.animation.json");
    }

    @Override
    public void setCustomAnimations(ShadeMawEntity anim, long instanceId, AnimationState<ShadeMawEntity> state) {
        super.setCustomAnimations(anim, instanceId, state);

        final GeoBone entityBone = getAnimationProcessor().getBone("entity");
        if (entityBone == null) {
            return;
        }

        if (anim.isInAnyFluid()) {
            float pitch = Mth.lerp(state.getPartialTick(), anim.xRotO, anim.getXRot());
            pitch = Mth.clamp(pitch, -90f, 90f);
            entityBone.setRotX(-pitch * Mth.DEG_TO_RAD);
            return;
        }

        final float jumpPitchDeg = Mth.lerp(state.getPartialTick(), anim.prevBodyPitch, anim.bodyPitch);
        if (Math.abs(jumpPitchDeg) > 0.05f) {
            entityBone.setRotX(-jumpPitchDeg * Mth.DEG_TO_RAD * 2);
        }

    }

}
