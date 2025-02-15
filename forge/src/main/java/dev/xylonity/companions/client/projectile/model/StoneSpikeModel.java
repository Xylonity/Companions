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
        if (animatable.tickCount <= 22) return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike.png");
        switch (animatable.tickCount) {
            case 23, 24: {return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike1.png");}
            case 25, 26, 27: {return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike2.png");}
            case 28, 29, 30: {return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike3.png");}
            default:  {return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike4.png");}
        }
    }

    @Override
    public ResourceLocation getAnimationResource(StoneSpikeProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/stone_spike.animation.json");
    }

}
