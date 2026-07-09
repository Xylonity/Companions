package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.HolinessNaginataProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HolinessNaginataModel extends GeoModel<HolinessNaginataProjectile> {

    @Override
    public ResourceLocation getModelResource(HolinessNaginataProjectile animatable) {
        return Companions.of("geo/holiness_naginata.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HolinessNaginataProjectile animatable) {
        return Companions.of("textures/entity/his_holiness_0.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HolinessNaginataProjectile animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}
