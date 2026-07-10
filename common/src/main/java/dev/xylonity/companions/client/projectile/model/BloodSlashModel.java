package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.BloodSlashProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BloodSlashModel extends GeoModel<BloodSlashProjectile> {

    @Override
    public ResourceLocation getModelResource(BloodSlashProjectile animatable) {
        return Companions.of("geo/blood_slash.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BloodSlashProjectile animatable) {
        return Companions.of("textures/entity/blood_slash.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BloodSlashProjectile animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}
