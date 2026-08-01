package dev.xylonity.companions;

import dev.xylonity.companions.client.ClientProxy;
import dev.xylonity.companions.common.CommonProxy;
import dev.xylonity.companions.common.event.CompanionsServerEvents;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.knightlib.KnightLib;
import dev.xylonity.knightlib.api.config.ConfigComposer;
import dev.xylonity.knightlib.api.event.KnightLibEvents;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(Companions.MOD_ID)
public class CompanionsForge {

    public CompanionsForge() {
        KnightLib.initialize();

        Companions.PROXY = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

        KnightLibEvents.SERVER.register(CompanionsServerEvents.class);
        Companions.PROXY.registerClientEvents();

        ConfigComposer.registerConfig(Companions.MOD_ID, CompanionsConfig.class);

        Companions.init();
    }

}
