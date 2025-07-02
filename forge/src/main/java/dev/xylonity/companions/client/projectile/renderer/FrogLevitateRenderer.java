package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.client.projectile.model.FrogLevitateModel;
import dev.xylonity.companions.common.entity.projectile.FrogLevitateProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FrogLevitateRenderer extends GeoEntityRenderer<FrogLevitateProjectile> {

    public FrogLevitateRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FrogLevitateModel());
    }

}