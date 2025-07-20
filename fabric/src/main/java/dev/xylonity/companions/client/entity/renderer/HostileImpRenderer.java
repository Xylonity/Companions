package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.HostileImpModel;
import dev.xylonity.companions.common.entity.hostile.HostileImpEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HostileImpRenderer extends GeoEntityRenderer<HostileImpEntity> {

    public HostileImpRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HostileImpModel());
        this.shadowRadius = 0.6f;
    }

}