package dev.xylonity.companions.client.item.renderer;

import dev.xylonity.companions.client.item.model.GenericItemModel;
import dev.xylonity.companions.common.item.gecko.GeckoItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericItemRenderer extends GeoItemRenderer<GeckoItem> {
    public GenericItemRenderer(String resourceKey) {
        super(new GenericItemModel(resourceKey));
    }
}