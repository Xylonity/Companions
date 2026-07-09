package dev.xylonity.companions.client.item.renderer;

import dev.xylonity.companions.client.item.model.GenericSwordItemModel;
import dev.xylonity.companions.common.item.gecko.GeckoSwordItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericSwordItemRenderer extends AbstractGeoItemRenderer<GeckoSwordItem> {

    public GenericSwordItemRenderer(String resourceKey) {
        super(new GenericSwordItemModel(resourceKey), resourceKey);
    }

}