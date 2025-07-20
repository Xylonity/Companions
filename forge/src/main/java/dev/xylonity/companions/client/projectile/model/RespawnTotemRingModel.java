package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.RespawnTotemRingProjectile;
import dev.xylonity.companions.common.entity.projectile.ShadeAltarUpgradeHaloProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RespawnTotemRingModel extends GeoModel<RespawnTotemRingProjectile> {

    @Override
    public ResourceLocation getModelResource(RespawnTotemRingProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/respawn_totem_ring.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RespawnTotemRingProjectile animatable) {
        int frames = 20;
        int perTick = 2;

        int frameIndex = (animatable.tickCount / perTick) % frames;
        return new ResourceLocation(Companions.MOD_ID, String.format("textures/entity/respawn_totem_ring_%d.png", frameIndex));
    }

    @Override
    public ResourceLocation getAnimationResource(RespawnTotemRingProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}
