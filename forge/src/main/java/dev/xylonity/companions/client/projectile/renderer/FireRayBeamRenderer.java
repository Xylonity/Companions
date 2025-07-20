package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.client.projectile.model.FireRayBeamModel;
import dev.xylonity.companions.common.entity.projectile.trigger.FireRayBeamEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireRayBeamRenderer extends GeoEntityRenderer<FireRayBeamEntity> {

    public FireRayBeamRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireRayBeamModel());
    }

}