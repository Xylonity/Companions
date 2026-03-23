package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.BlackHoleProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlackHoleModel extends GeoModel<BlackHoleProjectile> {

    @Override
    public ResourceLocation getModelResource(BlackHoleProjectile animatable) {
        return Companions.of("geo/black_hole.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlackHoleProjectile animatable) {
        if (animatable.getTickCount() == 18 || animatable.getTickCount() == 19) return Companions.of("textures/entity/black_hole_white.png");
        if (animatable.getTickCount() == 20 || animatable.getTickCount() == 21) return Companions.of("textures/entity/black_hole_black.png");

        return Companions.of("textures/entity/black_hole.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlackHoleProjectile animatable) {
        return Companions.of("animations/black_hole.animation.json");
    }

}
