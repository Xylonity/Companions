package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.BloodSlashProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BloodSlashModel extends GeoModel<BloodSlashProjectile> {

    @Override
    public ResourceLocation getModelResource(BloodSlashProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/blood_slash.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BloodSlashProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/blood_slash.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BloodSlashProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}
