package dev.xylonity.companions.common.event;

import com.mojang.serialization.Codec;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.blockentity.renderer.TeslaReceiverRenderer;
import dev.xylonity.companions.client.entity.renderer.*;
import dev.xylonity.companions.client.projectile.renderer.*;
import dev.xylonity.companions.common.entity.projectile.FireMarkRingProjectile;
import dev.xylonity.companions.common.particle.*;
import dev.xylonity.companions.registry.CompanionsBlockEntities;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
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
        EntityRenderers.register(CompanionsEntities.ILLAGER_GOLEM.get(), IllagerGolemRenderer::new);
        EntityRenderers.register(CompanionsEntities.DINAMO.get(), DinamoRenderer::new);
        EntityRenderers.register(CompanionsEntities.BROKEN_DINAMO.get(), BrokenDinamoRenderer::new);

        EntityRenderers.register(CompanionsEntities.SMALL_ICE_SHARD_PROJECTILE.get(), IceShardSmallRenderer::new);
        EntityRenderers.register(CompanionsEntities.BIG_ICE_SHARD_PROJECTILE.get(), IceShardBigRenderer::new);
        EntityRenderers.register(CompanionsEntities.TORNADO_PROJECTILE.get(), TornadoRenderer::new);
        EntityRenderers.register(CompanionsEntities.FIRE_MARK_PROJECTILE.get(), FireMarkRenderer::new);
        EntityRenderers.register(CompanionsEntities.FIRE_MARK_RING_PROJECTILE.get(), FireMarkRingRenderer::new);
        EntityRenderers.register(CompanionsEntities.STONE_SPIKE_PROJECTILE.get(), StoneSpikeRenderer::new);

        BlockEntityRenderers.register(CompanionsBlockEntities.TESLA_RECEIVER.get(), TeslaReceiverRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(CompanionsParticles.TEDDY_TRANSFORMATION.get(), TeddyTransformationParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.TEDDY_TRANSFORMATION_CLOUD.get(), TeddyTransformationCloudParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.ILLAGER_GOLEM_SPARK.get(), IllagerGolemSparkParticle.Provider::new);
        event.registerSpriteSet(CompanionsParticles.DINAMO_SPARK.get(), IllagerGolemSparkParticle.Provider::new);
    }

}
