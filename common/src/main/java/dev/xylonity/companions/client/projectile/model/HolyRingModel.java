package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.HolyRingProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HolyRingModel extends GeoModel<HolyRingProjectile> {

    private static final int FRAME_TICKS = 1;
    private static final ResourceLocation[] FRAME_TEXTURES = {
            Companions.of("textures/entity/blue_ring_0.png"),
            Companions.of("textures/entity/blue_ring_1.png"),
            Companions.of("textures/entity/blue_ring_2.png"),
            Companions.of("textures/entity/blue_ring_3.png"),
            Companions.of("textures/entity/blue_ring_4.png"),
            Companions.of("textures/entity/blue_ring_5.png"),
            Companions.of("textures/entity/blue_ring_6.png"),
            Companions.of("textures/entity/blue_ring_7.png"),
            Companions.of("textures/entity/blue_ring_8.png")
    };

    @Override
    public ResourceLocation getModelResource(HolyRingProjectile animatable) {
        return Companions.of("geo/blue_ring.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HolyRingProjectile animatable) {
        return FRAME_TEXTURES[(animatable.tickCount / FRAME_TICKS) % FRAME_TEXTURES.length];
    }

    @Override
    public ResourceLocation getAnimationResource(HolyRingProjectile animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}
