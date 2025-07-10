package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.client.projectile.model.LaserModel;
import dev.xylonity.companions.common.entity.projectile.trigger.LaserTriggerProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LaserRenderer extends GeoEntityRenderer<LaserTriggerProjectile> {

    public LaserRenderer(EntityRendererProvider.Context context) {
        super(context, new LaserModel());
    }

}
