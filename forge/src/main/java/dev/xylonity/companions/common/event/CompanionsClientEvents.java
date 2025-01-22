package dev.xylonity.companions.common.event;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.renderer.AntlionRenderer;
import dev.xylonity.companions.client.entity.renderer.FroggyRenderer;
import dev.xylonity.companions.client.entity.renderer.TeddyRenderer;
import dev.xylonity.companions.common.particle.TeddyTransformationCloudParticle;
import dev.xylonity.companions.common.particle.TeddyTransformationParticle;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CompanionsCommon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CompanionsClientEvents {

    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        EntityRenderers.register(CompanionsEntities.FROGGY.get(), FroggyRenderer::new);
        EntityRenderers.register(CompanionsEntities.TEDDY.get(), TeddyRenderer::new);
        EntityRenderers.register(CompanionsEntities.ANTLION.get(), AntlionRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(CompanionsParticles.TEDDY_TRANSFORMATION.get(), TeddyTransformationParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.TEDDY_TRANSFORMATION_CLOUD.get(), TeddyTransformationCloudParticle.Provider::new);
    }

}
