package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.client.blockentity.model.HolyPorcelainPotteryModel;
import dev.xylonity.companions.client.blockentity.model.PorcelainPotteryModel;
import dev.xylonity.companions.common.blockentity.HolyPorcelainPotteryBlockEntity;
import dev.xylonity.companions.common.blockentity.PorcelainPotteryBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class HolyPorcelainPotteryRenderer extends GeoBlockRenderer<HolyPorcelainPotteryBlockEntity> {

    public HolyPorcelainPotteryRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new HolyPorcelainPotteryModel());
    }

}