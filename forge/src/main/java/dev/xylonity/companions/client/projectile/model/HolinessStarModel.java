package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.HolinessStartProjectile;
import dev.xylonity.companions.common.entity.projectile.HolinessStartProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HolinessStarModel extends GeoModel<HolinessStartProjectile> {

    @Override
    public ResourceLocation getModelResource(HolinessStartProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/black_hole.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HolinessStartProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/black_hole.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HolinessStartProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/black_hole.animation.json");
    }

}
