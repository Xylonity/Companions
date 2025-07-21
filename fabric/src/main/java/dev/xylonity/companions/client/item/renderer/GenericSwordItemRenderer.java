package dev.xylonity.companions.client.item.renderer;

import dev.xylonity.companions.client.item.model.GenericSwordItemModel;
import dev.xylonity.companions.common.item.generic.GenericGeckoSwordItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericSwordItemRenderer extends GeoItemRenderer<GenericGeckoSwordItem> {
    public GenericSwordItemRenderer(String resourceKey) {
        super(new GenericSwordItemModel(resourceKey));
    }
}