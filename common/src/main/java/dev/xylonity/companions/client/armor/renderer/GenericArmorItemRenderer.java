package dev.xylonity.companions.client.armor.renderer;

import dev.xylonity.companions.client.armor.model.GenericArmorItemModel;
import dev.xylonity.companions.common.item.gecko.GeckoArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class GenericArmorItemRenderer extends GeoArmorRenderer<GeckoArmorItem> {
    public GenericArmorItemRenderer(String resourceKey) {
        super(new GenericArmorItemModel(resourceKey));
    }
}