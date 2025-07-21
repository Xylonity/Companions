package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.client.projectile.model.AntlionSandProjectileModel;
import dev.xylonity.companions.common.entity.projectile.AntlionSandProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AntlionSandProjectileRenderer extends GeoEntityRenderer<AntlionSandProjectile> {

    public AntlionSandProjectileRenderer(EntityRendererProvider.Context context) {
        super(context, new AntlionSandProjectileModel());
    }

}
