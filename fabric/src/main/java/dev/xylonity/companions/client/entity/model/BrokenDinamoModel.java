package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.hostile.BrokenDinamoEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BrokenDinamoModel extends GeoModel<BrokenDinamoEntity> {

    @Override
    public ResourceLocation getModelResource(BrokenDinamoEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/broken_dinamo.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BrokenDinamoEntity animatable) {
        if (animatable.getState() >= 4) {
            return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/dinamo.png");
        }

        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/illager_golem.png");
    }

    @Override
    public void setCustomAnimations(BrokenDinamoEntity animatable, long instanceId, AnimationState<BrokenDinamoEntity> animationState) {

        Entity camera = Minecraft.getInstance().getCameraEntity();
        if (camera == null) return;

        float partialTick = animationState.getPartialTick();

        Vec3 cameraPos = camera.getEyePosition(partialTick);
        Vec3 entityPos = animatable.getEyePosition(partialTick);

        Vec3 diff = new Vec3(cameraPos.x - entityPos.x, 0.0, cameraPos.z - entityPos.z).normalize();
        Vec3 view;

        if (animatable.getState() == 0) {
            view = animatable.getViewVector(partialTick).yRot((float) Math.toRadians(32.5)).normalize();
        } else {
            view = animatable.getViewVector(partialTick).normalize();
        }

        view = new Vec3(view.x, 0.0, view.z).normalize();

        Vec3 lateral = new Vec3(-view.z, 0.0, view.x);

        float dot = (float)(diff.x * lateral.x + diff.z * lateral.z);
        dot = Mth.clamp(dot, -1f, 1f);

        CoreGeoBone leftEyeBone = this.getAnimationProcessor().getBone("left_eye");
        CoreGeoBone rightEyeBone = this.getAnimationProcessor().getBone("right_eye");

        float baseLeftEyeX = -0.0f;
        float baseRightEyeX = 0.0f;

        if (leftEyeBone != null) {
            leftEyeBone.setPosX(baseLeftEyeX - dot);
        }

        if (rightEyeBone != null) {
            rightEyeBone.setPosX(baseRightEyeX - dot);
        }

        super.setCustomAnimations(animatable, instanceId, animationState);
    }

    @Override
    public ResourceLocation getAnimationResource(BrokenDinamoEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/illager_golem.animation.json");
    }

}