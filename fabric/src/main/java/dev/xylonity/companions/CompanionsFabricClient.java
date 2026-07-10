package dev.xylonity.companions;

import dev.xylonity.companions.client.ClientProxy;
import net.fabricmc.api.ClientModInitializer;

public class CompanionsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Companions.PROXY = new ClientProxy();
        Companions.PROXY.registerClientEvents();
    }

}
