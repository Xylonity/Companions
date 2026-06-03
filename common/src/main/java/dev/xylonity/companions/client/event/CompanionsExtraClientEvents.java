package dev.xylonity.companions.client.event;

import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.knightlib.api.event.RegisterEvent;
import dev.xylonity.knightlib.api.event.impl.client.RenderLayerRegistrationEvent;

public class CompanionsExtraClientEvents {

    @RegisterEvent
    public static void registerBlockRenderTypes(final RenderLayerRegistrationEvent event) {
        event.setCutout(CompanionsBlocks.ETERNAL_FIRE.get());
    }

}
