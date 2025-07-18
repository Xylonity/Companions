package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.BrokenDinamoModel;
import dev.xylonity.companions.common.entity.hostile.BrokenDinamoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BrokenDinamoRenderer extends GeoEntityRenderer<BrokenDinamoEntity> {

    public BrokenDinamoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BrokenDinamoModel());
        this.shadowRadius = 0.7f;
    }

}
