package dev.xylonity.companions.client.item.renderer;

import dev.xylonity.companions.client.item.model.GenericAxeItemModel;
import dev.xylonity.companions.common.item.generic.GenericGeckoAxeItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericAxeItemRenderer extends GeoItemRenderer<GenericGeckoAxeItem> {
    public GenericAxeItemRenderer(String resourceKey) {
        super(new GenericAxeItemModel(resourceKey));
    }
}