package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.projectile.FireMarkRingProjectile;
import dev.xylonity.companions.common.entity.projectile.StoneSpikeProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StoneSpikeModel extends GeoModel<StoneSpikeProjectile> {

    @Override
    public ResourceLocation getModelResource(StoneSpikeProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/stone_spike.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StoneSpikeProjectile animatable) {
        int l = animatable.getLifetime();
        int remaining = l - animatable.tickCount;

        return switch (remaining) {
            case 12, 11 -> new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike1.png");
            case 10, 9 -> new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike2.png");
            case 8, 7 -> new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike3.png");
            case 6, 5 -> new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike4.png");
            case 4, 3 -> new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike5.png");
            case 2, 1 -> new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike6.png");
            default -> new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike0.png");
        };
    }

    @Override
    public ResourceLocation getAnimationResource(StoneSpikeProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/stone_spike.animation.json");
    }

}
