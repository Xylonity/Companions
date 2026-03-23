package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.NeedleProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NeedleModel extends GeoModel<NeedleProjectile> {

    @Override
    public ResourceLocation getModelResource(NeedleProjectile animatable) {
        return Companions.of("geo/needle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(NeedleProjectile animatable) {
        return Companions.of("textures/entity/needle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(NeedleProjectile animatable) {
        return Companions.of("animations/needle.animation.json");
    }

}
