package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.client.blockentity.model.GenericBlockItemModel;
import dev.xylonity.companions.common.item.blockitem.GenericBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericBlockItemRenderer extends GeoItemRenderer<GenericBlockItem> {
    public GenericBlockItemRenderer() {
        super(new GenericBlockItemModel());
    }

}
