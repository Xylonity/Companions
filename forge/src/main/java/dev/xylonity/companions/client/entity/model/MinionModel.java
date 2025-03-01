package dev.xylonity.companions.client.entity.model;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.renderer.MinionRenderer;
import dev.xylonity.companions.common.entity.custom.BrokenDinamoEntity;
import dev.xylonity.companions.common.entity.custom.HostileImpEntity;
import dev.xylonity.companions.common.entity.custom.MinionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class MinionModel extends GeoModel<MinionEntity> {

    @Override
    public ResourceLocation getModelResource(MinionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "geo/" + animatable.getVariant() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MinionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/" + animatable.getVariant() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(MinionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/" + animatable.getVariant() + ".animation.json");
    }

}