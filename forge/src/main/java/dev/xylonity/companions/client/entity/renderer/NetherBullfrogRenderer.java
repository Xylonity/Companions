package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.FireworkToadModel;
import dev.xylonity.companions.client.entity.model.NetherBullfrogModel;
import dev.xylonity.companions.common.entity.summon.FireworkToadEntity;
import dev.xylonity.companions.common.entity.summon.NetherBullfrogEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NetherBullfrogRenderer extends GeoEntityRenderer<NetherBullfrogEntity> {

    public NetherBullfrogRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new NetherBullfrogModel());
    }

}