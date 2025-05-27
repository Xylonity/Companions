package dev.xylonity.companions.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import dev.xylonity.companions.common.entity.custom.BrokenDinamoEntity;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SacredPontiffModel extends GeoModel<SacredPontiffEntity> {

    @Override
    public ResourceLocation getModelResource(SacredPontiffEntity animatable) {
        if (animatable.getPhase() == 2) {
            return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/his_holiness.geo.json");
        }

        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/sacred_pontiff.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SacredPontiffEntity animatable) {
        if (animatable.getPhase() == 2) {
            return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/his_holiness.png");
        }

        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/sacred_pontiff.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SacredPontiffEntity animatable) {
        if (animatable.getPhase() == 2) {
            return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/his_holiness.animation.json");
        }

        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/sacred_pontiff.animation.json");
    }

    @Override
    public void setCustomAnimations(SacredPontiffEntity animatable, long instanceId, AnimationState<SacredPontiffEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY((entityData.netHeadYaw() * 0.5f) * Mth.DEG_TO_RAD);
        }

        Entity camera = Minecraft.getInstance().getCameraEntity();
        if (camera == null) return;

        float partialTick = animationState.getPartialTick();

        Vec3 cameraPos = camera.getEyePosition(partialTick);
        Vec3 entityPos = animatable.getEyePosition(partialTick);

        Vec3 diff = new Vec3(cameraPos.x - entityPos.x, 0.0, cameraPos.z - entityPos.z).normalize();

        Vec3 view = animatable.getViewVector(partialTick).normalize();
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

}