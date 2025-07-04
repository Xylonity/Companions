package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.AntlionModel;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AntlionRenderer extends GeoEntityRenderer<AntlionEntity> {

    public AntlionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AntlionModel());
    }

}