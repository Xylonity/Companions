package dev.xylonity.companions;

import dev.xylonity.companions.client.ClientProxy;
import dev.xylonity.companions.client.event.CompanionsClientEvents;
import dev.xylonity.knightlib.api.event.KnightLibEvents;
import net.fabricmc.api.ClientModInitializer;

public class CompanionsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Companions.PROXY = new ClientProxy();
        KnightLibEvents.CLIENT.register(CompanionsClientEvents.class);
    }

}
