package dev.xylonity.companions.client.item.renderer;

import dev.xylonity.companions.client.item.model.GenericAxeItemModel;
import dev.xylonity.companions.common.item.gecko.GeckoAxeItem;

public class GenericAxeItemRenderer extends AbstractGeoItemRenderer<GeckoAxeItem> {

    public GenericAxeItemRenderer(String resourceKey) {
        super(new GenericAxeItemModel(resourceKey), resourceKey);
    }

}