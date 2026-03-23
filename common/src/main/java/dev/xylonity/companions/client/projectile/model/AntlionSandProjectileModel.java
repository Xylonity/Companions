package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.AntlionSandProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AntlionSandProjectileModel extends GeoModel<AntlionSandProjectile> {

    @Override
    public ResourceLocation getModelResource(AntlionSandProjectile animatable) {
        return Companions.of("geo/antlion_sand_projectile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AntlionSandProjectile animatable) {
        return Companions.of("textures/entity/antlion_sand_projectile.png");
    }

    @Override
    public ResourceLocation getAnimationResource(AntlionSandProjectile animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}
