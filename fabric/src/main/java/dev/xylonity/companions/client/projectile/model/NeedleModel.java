package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.entity.projectile.NeedleProjectile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NeedleModel extends GeoModel<NeedleProjectile> {

    @Override
    public ResourceLocation getModelResource(NeedleProjectile animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/needle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(NeedleProjectile animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/needle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(NeedleProjectile animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "animations/needle.animation.json");
    }

}
