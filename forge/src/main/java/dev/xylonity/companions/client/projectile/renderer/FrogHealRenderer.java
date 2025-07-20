package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.client.projectile.model.FrogHealModel;
import dev.xylonity.companions.common.entity.projectile.FrogHealProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FrogHealRenderer extends GeoEntityRenderer<FrogHealProjectile> {

    public FrogHealRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FrogHealModel());
    }

}