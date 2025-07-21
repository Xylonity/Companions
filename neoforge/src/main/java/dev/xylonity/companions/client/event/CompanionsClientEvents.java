package dev.xylonity.companions.client.event;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.blockentity.renderer.*;
import dev.xylonity.companions.client.entity.renderer.*;
import dev.xylonity.companions.client.gui.screen.CorneliusScreen;
import dev.xylonity.companions.client.gui.screen.PuppetScreen;
import dev.xylonity.companions.client.gui.screen.SoulFurnaceScreen;
import dev.xylonity.companions.client.gui.screen.SoulMageScreen;
import dev.xylonity.companions.client.projectile.renderer.*;
import dev.xylonity.companions.common.particle.*;
import dev.xylonity.companions.registry.*;
import dev.xylonity.knightlib.api.BossBarBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public class CompanionsClientEvents {

    @EventBusSubscriber(modid = Companions.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class CompanionsClientModBus {

        private static final ResourceLocation PONTIFF_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/gui/pontiff_bar_background.png");
        private static final ResourceLocation PONTIFF_HEALTH = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/gui/pontiff_bar_health.png");
        private static final ResourceLocation PONTIFF_OVERLAY = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/gui/pontiff_bar_overlay.png");

        private static final ResourceLocation HOLINESS_BACKGROUND = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/gui/holiness_bar_background.png");
        private static final ResourceLocation HOLINESS_HEALTH = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/gui/holiness_bar_health.png");
        private static final ResourceLocation HOLINESS_OVERLAY = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/gui/holiness_bar_overlay.png");

        @SubscribeEvent
        public static void registerEntityRenderers(FMLClientSetupEvent event) {
            EntityRenderers.register(CompanionsEntities.CORNELIUS.get(), CorneliusRenderer::new);
            EntityRenderers.register(CompanionsEntities.TEDDY.get(), TeddyRenderer::new);
            EntityRenderers.register(CompanionsEntities.ANTLION.get(), AntlionRenderer::new);
            EntityRenderers.register(CompanionsEntities.DINAMO.get(), DinamoRenderer::new);
            EntityRenderers.register(CompanionsEntities.BROKEN_DINAMO.get(), BrokenDinamoRenderer::new);
            EntityRenderers.register(CompanionsEntities.MINION.get(), MinionRenderer::new);
            EntityRenderers.register(CompanionsEntities.GOLDEN_ALLAY.get(), GoldenAllayRenderer::new);
            EntityRenderers.register(CompanionsEntities.SOUL_MAGE.get(), SoulMageRenderer::new);
            EntityRenderers.register(CompanionsEntities.LIVING_CANDLE.get(), LivingCandleRenderer::new);
            EntityRenderers.register(CompanionsEntities.CROISSANT_DRAGON.get(), CroissantDragonRenderer::new);
            EntityRenderers.register(CompanionsEntities.PUPPET_GLOVE.get(), PuppetGloveRenderer::new);
            EntityRenderers.register(CompanionsEntities.PUPPET.get(), PuppetRenderer::new);
            EntityRenderers.register(CompanionsEntities.SHADE_SWORD.get(), ShadeSwordRenderer::new);
            EntityRenderers.register(CompanionsEntities.SHADE_MAW.get(), ShadeMawRenderer::new);
            EntityRenderers.register(CompanionsEntities.MANKH.get(), MankhRenderer::new);
            EntityRenderers.register(CompanionsEntities.CLOAK.get(), CloakRenderer::new);

            EntityRenderers.register(CompanionsEntities.FIREWORK_TOAD.get(), FireworkToadRenderer::new);
            EntityRenderers.register(CompanionsEntities.NETHER_BULLFROG.get(), NetherBullfrogRenderer::new);
            EntityRenderers.register(CompanionsEntities.ENDER_FROG.get(), EnderFrogRenderer::new);
            EntityRenderers.register(CompanionsEntities.EMBER_POLE.get(), EmberPoleRenderer::new);
            EntityRenderers.register(CompanionsEntities.BUBBLE_FROG.get(), BubbleFrogRenderer::new);

            EntityRenderers.register(CompanionsEntities.ILLAGER_GOLEM.get(), IllagerGolemRenderer::new);
            EntityRenderers.register(CompanionsEntities.HOSTILE_PUPPET_GLOVE.get(), HostilePuppetGloveRenderer::new);
            EntityRenderers.register(CompanionsEntities.HOSTILE_IMP.get(), HostileImpRenderer::new);
            EntityRenderers.register(CompanionsEntities.SACRED_PONTIFF.get(), SacredPontiffRenderer::new);
            EntityRenderers.register(CompanionsEntities.WILD_ANTLION.get(), WildAntlionRenderer::new);

            EntityRenderers.register(CompanionsEntities.SMALL_ICE_SHARD_PROJECTILE.get(), IceShardSmallRenderer::new);
            EntityRenderers.register(CompanionsEntities.BIG_ICE_SHARD_PROJECTILE.get(), IceShardBigRenderer::new);
            EntityRenderers.register(CompanionsEntities.TORNADO_PROJECTILE.get(), TornadoRenderer::new);
            EntityRenderers.register(CompanionsEntities.BLOOD_TORNADO_PROJECTILE.get(), BloodTornadoRenderer::new);
            EntityRenderers.register(CompanionsEntities.FIRE_MARK_PROJECTILE.get(), FireMarkRenderer::new);
            EntityRenderers.register(CompanionsEntities.FIRE_MARK_RING_PROJECTILE.get(), FireMarkRingRenderer::new);
            EntityRenderers.register(CompanionsEntities.STONE_SPIKE_PROJECTILE.get(), StoneSpikeRenderer::new);
            EntityRenderers.register(CompanionsEntities.HEAL_RING_PROJECTILE.get(), HealRingRenderer::new);
            EntityRenderers.register(CompanionsEntities.BRACE_PROJECTILE.get(), BraceRenderer::new);
            EntityRenderers.register(CompanionsEntities.MAGIC_RAY_PIECE_PROJECTILE.get(), MagicRayPieceRenderer::new);
            EntityRenderers.register(CompanionsEntities.FIRE_RAY_PIECE_PROJECTILE.get(), FireRayPieceRenderer::new);
            EntityRenderers.register(CompanionsEntities.MAGIC_RAY_PIECE_CIRCLE_PROJECTILE.get(), MagicRayCircleRenderer::new);
            EntityRenderers.register(CompanionsEntities.BLACK_HOLE_PROJECTILE.get(), BlackHoleRenderer::new);
            EntityRenderers.register(CompanionsEntities.SOUL_MAGE_BOOK.get(), SoulMageBookRenderer::new);
            EntityRenderers.register(CompanionsEntities.FLOOR_CAKE_CREAM.get(), FloorCakeCreamRenderer::new);
            EntityRenderers.register(CompanionsEntities.STAKE_PROJECTILE.get(), StakeRenderer::new);
            EntityRenderers.register(CompanionsEntities.HOLINESS_NAGINATA.get(), HolinessNaginataRenderer::new);
            EntityRenderers.register(CompanionsEntities.HOLINESS_NAGINATA.get(), HolinessNaginataRenderer::new);
            EntityRenderers.register(CompanionsEntities.HOLINESS_STAR.get(), HolinessStarRenderer::new);
            EntityRenderers.register(CompanionsEntities.SHADE_ALTAR_UPGRADE_HALO.get(), ShadeAltarUpgradeHaloRenderer::new);
            EntityRenderers.register(CompanionsEntities.SHADE_SWORD_IMPACT_PROJECTILE.get(), ShadeSwordImpactRenderer::new);
            EntityRenderers.register(CompanionsEntities.PONTIFF_FIRE_RING.get(), PontiffFireRingRenderer::new);
            EntityRenderers.register(CompanionsEntities.NEEDLE_PROJECTILE.get(), NeedleRenderer::new);
            EntityRenderers.register(CompanionsEntities.RESPAWN_TOTEM_RING_PROJECTILE.get(), RespawnTotemRingRenderer::new);
            EntityRenderers.register(CompanionsEntities.LASER_PROJECTILE.get(), LaserRenderer::new);
            EntityRenderers.register(CompanionsEntities.LASER_RING.get(), LaserRingRenderer::new);
            EntityRenderers.register(CompanionsEntities.SCROLL.get(), ScrollRenderer::new);
            EntityRenderers.register(CompanionsEntities.FROG_HEAL_PROJECTILE.get(), FrogHealRenderer::new);
            EntityRenderers.register(CompanionsEntities.RED_STAR_EXPLOSION.get(), RedStarExplosionRenderer::new);
            EntityRenderers.register(CompanionsEntities.RED_STAR_EXPLOSION_CENTER.get(), RedStarExplosionCenterRenderer::new);
            EntityRenderers.register(CompanionsEntities.BLUE_STAR_EXPLOSION.get(), BlueStarExplosionRenderer::new);
            EntityRenderers.register(CompanionsEntities.BLUE_STAR_EXPLOSION_CENTER.get(), BlueStarExplosionCenterRenderer::new);
            EntityRenderers.register(CompanionsEntities.FROG_LEVITATE_PROJECTILE.get(), FrogLevitateRenderer::new);
            EntityRenderers.register(CompanionsEntities.FROG_EGG_PROJECTILE.get(), FrogEggRenderer::new);
            EntityRenderers.register(CompanionsEntities.ANTLION_SAND_PROJECTILE.get(), AntlionSandProjectileRenderer::new);
            EntityRenderers.register(CompanionsEntities.BLOOD_SLASH_PROJECTILE.get(), BloodSlashRenderer::new);

            EntityRenderers.register(CompanionsEntities.GENERIC_TRIGGER_PROJECTILE.get(), GenericTriggerProjectileRenderer::new);
            EntityRenderers.register(CompanionsEntities.CAKE_CREAM_TRIGGER_PROJECTILE.get(), GenericTriggerProjectileRenderer::new);
            EntityRenderers.register(CompanionsEntities.FIRE_RAY_BEAM_ENTITY.get(), FireRayBeamRenderer::new);

            BlockEntityRenderers.register(CompanionsBlockEntities.TESLA_COIL.get(), TeslaCoilRenderer::new);
            BlockEntityRenderers.register(CompanionsBlockEntities.SOUL_FURNACE.get(), SoulFurnaceRenderer::new);
            BlockEntityRenderers.register(CompanionsBlockEntities.CROISSANT_EGG.get(), CroissantEggRenderer::new);
            BlockEntityRenderers.register(CompanionsBlockEntities.PLASMA_LAMP.get(), PlasmaLampRenderer::new);
            BlockEntityRenderers.register(CompanionsBlockEntities.VOLTAIC_PILLAR.get(), VoltaicPillarRenderer::new);
            BlockEntityRenderers.register(CompanionsBlockEntities.EMPTY_PUPPET.get(), EmptyPuppetRenderer::new);
            BlockEntityRenderers.register(CompanionsBlockEntities.RESPAWN_TOTEM.get(), RespawnTotemRenderer::new);
            BlockEntityRenderers.register(CompanionsBlockEntities.SHADE_SWORD_ALTAR.get(), ShadeSwordAltarRenderer::new);
            BlockEntityRenderers.register(CompanionsBlockEntities.SHADE_MAW_ALTAR.get(), ShadeMawAltarRenderer::new);
            BlockEntityRenderers.register(CompanionsBlockEntities.RECALL_PLATFORM.get(), RecallPlatformRenderer::new);
            BlockEntityRenderers.register(CompanionsBlockEntities.VOLTAIC_RELAY.get(), VoltaicRelayRenderer::new);
            BlockEntityRenderers.register(CompanionsBlockEntities.FROG_BONANZA.get(), FrogBonanzaRenderer::new);

            ItemBlockRenderTypes.setRenderLayer(CompanionsBlocks.ETERNAL_FIRE.get(), RenderType.cutout());

            // Pontiff
            BossBarBuilder
                .matcher(boss -> boss.getName().getString().contains("Sacred Pontiff") && !boss.getName().getString().equalsIgnoreCase("Sacred Pontiff Invisible"))
                .renderer((gui, boss, x, y) -> {
                    // background
                    gui.blit(PONTIFF_BACKGROUND, x, y + 22, 0, 0, 183, 5);

                    // health
                    int healthWidth = (int) (boss.getProgress() * 183);
                    if (healthWidth > 0) {
                        gui.blit(PONTIFF_HEALTH, x, y, 0, 0, healthWidth, 27);
                    }

                    // overlay
                    gui.blit(PONTIFF_OVERLAY, x - 3, y, 0, 0, 189, 30);

                    int text = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - Minecraft.getInstance().font.width(boss.getName()) / 2;
                    // name
                    gui.drawString(Minecraft.getInstance().font, boss.getName(), text, y, 0xFFFFFF);
                })
                .padding(24)
                .hideVanillaName()
                .register();

            // Invisible
            BossBarBuilder
                .matcher(boss -> boss.getName().getString().contains("Sacred Pontiff Invisible"))
                .renderer((gui, boss, x, y) -> { ;; })
                .hideVanillaName()
                .register();

            // Holiness
            BossBarBuilder
                .matcher(boss -> boss.getName().getString().contains("His Holiness"))
                .renderer((gui, boss, x, y) -> {
                    // background
                    gui.blit(HOLINESS_BACKGROUND, x - 2, y + 15, 0, 0, 185, 5);

                    // health
                    int healthWidth = (int) (boss.getProgress() * 185);
                    if (healthWidth > 0) {
                        gui.blit(HOLINESS_HEALTH, x - 2, y + 15, 0, 0, healthWidth, 28);
                    }

                    // overlay
                    gui.blit(HOLINESS_OVERLAY, x - 23, y, 0, 0, 227, 42);

                    int text = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - Minecraft.getInstance().font.width(boss.getName()) / 2;
                    gui.drawString(Minecraft.getInstance().font, boss.getName(), text, y, 0xFFFFFF);
                })
                .padding(24)
                .hideVanillaName()
                .register();
        }

        @SubscribeEvent
        public static void registerMenus(RegisterMenuScreensEvent event) {
            event.register(CompanionsMenuTypes.SOUL_FURNACE.get(), SoulFurnaceScreen::new);
            event.register(CompanionsMenuTypes.SOUL_MAGE_CONTAINER.get(), SoulMageScreen::new);
            event.register(CompanionsMenuTypes.PUPPET_CONTAINER.get(), PuppetScreen::new);
            event.register(CompanionsMenuTypes.CORNELIUS_CONTAINER.get(), CorneliusScreen::new);
        }

        @SubscribeEvent
        public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(CompanionsParticles.TEDDY_TRANSFORMATION.get(), TeddyTransformationParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.TEDDY_TRANSFORMATION_CLOUD.get(), TeddyTransformationCloudParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.ILLAGER_GOLEM_SPARK.get(), IllagerGolemSparkParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.DINAMO_SPARK.get(), IllagerGolemSparkParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.EMBER.get(), EmberParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.BLACK_HOLE_STAR.get(), BlackHoleStarParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.BLIZZARD_SNOW.get(), BlizzardSnowParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.BLIZZARD_ICE.get(), BlizzardIceParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.GOLDEN_ALLAY_TRAIL.get(), GoldenAllayTrailParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.CAKE_CREAM.get(), CakeCreamParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.CAKE_CREAM_STRAWBERRY.get(), CakeCreamParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.CAKE_CREAM_CHOCOLATE.get(), CakeCreamParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.SOUL_FLAME.get(), SoulFlameParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.FIREWORK_TOAD.get(), FireworkToadParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.SHADE_TRAIL.get(), ShadeTrailParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.SHADE_SUMMON.get(), ShadeSummonParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.HOLINESS_RED_STAR_TRAIL.get(), GoldenAllayTrailParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.HOLINESS_BLUE_STAR_TRAIL.get(), GoldenAllayTrailParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.RESPAWN_TOTEM.get(), GoldenAllayTrailParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.BLINK.get(), BlinkParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.LASER_SPARK.get(), IllagerGolemSparkParticle.Provider::new);
            event.registerSpriteSet(CompanionsParticles.EMBER_POLE_EXPLOSION.get(), TeddyTransformationParticle.Provider::new);
        }

    }

}
