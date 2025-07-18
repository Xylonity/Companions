package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.projectile.HolinessNaginataProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HolinessNaginataModel extends GeoModel<HolinessNaginataProjectile> {

    @Override
    public ResourceLocation getModelResource(HolinessNaginataProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/holiness_naginata.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HolinessNaginataProjectile animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/his_holiness.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HolinessNaginataProjectile animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/generic.animation.json");
    }

}
