package dev.xylonity.companions.client.event;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.blockentity.renderer.*;
import dev.xylonity.companions.client.entity.renderer.*;
import dev.xylonity.companions.client.gui.screen.CorneliusScreen;
import dev.xylonity.companions.client.gui.screen.PuppetScreen;
import dev.xylonity.companions.client.gui.screen.SoulFurnaceScreen;
import dev.xylonity.companions.client.gui.screen.SoulMageScreen;
import dev.xylonity.companions.client.projectile.renderer.*;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import dev.xylonity.companions.common.particle.*;
import dev.xylonity.companions.registry.*;
import dev.xylonity.knightlib.api.bossbar.BossBarBuilder;
import dev.xylonity.knightlib.api.event.RegisterEvent;
import dev.xylonity.knightlib.api.event.impl.client.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class CompanionsClientEvents {

    private static final ResourceLocation PONTIFF_BACKGROUND = Companions.of("textures/gui/pontiff_bar_background.png");
    private static final ResourceLocation PONTIFF_HEALTH = Companions.of("textures/gui/pontiff_bar_health.png");
    private static final ResourceLocation PONTIFF_OVERLAY = Companions.of("textures/gui/pontiff_bar_overlay.png");

    private static final ResourceLocation HOLINESS_BACKGROUND = Companions.of("textures/gui/holiness_bar_background.png");
    private static final ResourceLocation HOLINESS_HEALTH = Companions.of("textures/gui/holiness_bar_health.png");
    private static final ResourceLocation HOLINESS_OVERLAY = Companions.of("textures/gui/holiness_bar_overlay.png");

    @RegisterEvent
    public static void registerEntityRenderers(final EntityRendererRegistrationEvent event) {
        event.register(CompanionsEntities.CORNELIUS, CorneliusRenderer::new);
        event.register(CompanionsEntities.TEDDY, TeddyRenderer::new);
        event.register(CompanionsEntities.ANTLION, AntlionRenderer::new);
        event.register(CompanionsEntities.DINAMO, DinamoRenderer::new);
        event.register(CompanionsEntities.BROKEN_DINAMO, BrokenDinamoRenderer::new);
        event.register(CompanionsEntities.MINION, MinionRenderer::new);
        event.register(CompanionsEntities.GOLDEN_ALLAY, GoldenAllayRenderer::new);
        event.register(CompanionsEntities.SOUL_MAGE, SoulMageRenderer::new);
        event.register(CompanionsEntities.LIVING_CANDLE, LivingCandleRenderer::new);
        event.register(CompanionsEntities.CROISSANT_DRAGON, CroissantDragonRenderer::new);
        event.register(CompanionsEntities.PUPPET_GLOVE, PuppetGloveRenderer::new);
        event.register(CompanionsEntities.PUPPET, PuppetRenderer::new);
        event.register(CompanionsEntities.SHADE_SWORD, ShadeSwordRenderer::new);
        event.register(CompanionsEntities.SHADE_MAW, ShadeMawRenderer::new);
        event.register(CompanionsEntities.SHADE_BAT, ShadeBatRenderer::new);
        event.register(CompanionsEntities.SHADE_BAT_PART, ShadeBatPartRenderer::new);
        event.register(CompanionsEntities.MANKH, MankhRenderer::new);
        event.register(CompanionsEntities.CLOAK, CloakRenderer::new);

        event.register(CompanionsEntities.FIREWORK_TOAD, FireworkToadRenderer::new);
        event.register(CompanionsEntities.NETHER_BULLFROG, NetherBullfrogRenderer::new);
        event.register(CompanionsEntities.ENDER_FROG, EnderFrogRenderer::new);
        event.register(CompanionsEntities.EMBER_POLE, EmberPoleRenderer::new);
        event.register(CompanionsEntities.BUBBLE_FROG, BubbleFrogRenderer::new);

        event.register(CompanionsEntities.ILLAGER_GOLEM, IllagerGolemRenderer::new);
        event.register(CompanionsEntities.HOSTILE_PUPPET_GLOVE, HostilePuppetGloveRenderer::new);
        event.register(CompanionsEntities.HOSTILE_IMP, HostileImpRenderer::new);
        event.register(CompanionsEntities.SACRED_PONTIFF, SacredPontiffRenderer::new);
        event.register(CompanionsEntities.WILD_ANTLION, WildAntlionRenderer::new);

        event.register(CompanionsEntities.SMALL_ICE_SHARD_PROJECTILE, IceShardSmallRenderer::new);
        event.register(CompanionsEntities.BIG_ICE_SHARD_PROJECTILE, IceShardBigRenderer::new);
        event.register(CompanionsEntities.TORNADO_PROJECTILE, TornadoRenderer::new);
        event.register(CompanionsEntities.BLOOD_TORNADO_PROJECTILE, BloodTornadoRenderer::new);
        event.register(CompanionsEntities.FIRE_MARK_PROJECTILE, FireMarkRenderer::new);
        event.register(CompanionsEntities.FIRE_MARK_RING_PROJECTILE, FireMarkRingRenderer::new);
        event.register(CompanionsEntities.STONE_SPIKE_PROJECTILE, StoneSpikeRenderer::new);
        event.register(CompanionsEntities.HEAL_RING_PROJECTILE, HealRingRenderer::new);
        event.register(CompanionsEntities.BRACE_PROJECTILE, BraceRenderer::new);
        event.register(CompanionsEntities.MAGIC_RAY_PIECE_PROJECTILE, MagicRayPieceRenderer::new);
        event.register(CompanionsEntities.FIRE_RAY_PIECE_PROJECTILE, FireRayPieceRenderer::new);
        event.register(CompanionsEntities.MAGIC_RAY_PIECE_CIRCLE_PROJECTILE, MagicRayCircleRenderer::new);
        event.register(CompanionsEntities.BLACK_HOLE_PROJECTILE, BlackHoleRenderer::new);
        event.register(CompanionsEntities.SOUL_MAGE_BOOK, SoulMageBookRenderer::new);
        event.register(CompanionsEntities.FLOOR_CAKE_CREAM, FloorCakeCreamRenderer::new);
        event.register(CompanionsEntities.STAKE_PROJECTILE, StakeRenderer::new);
        event.register(CompanionsEntities.HOLINESS_NAGINATA, HolinessNaginataRenderer::new);
        event.register(CompanionsEntities.HOLINESS_STAR, HolinessStarRenderer::new);
        event.register(CompanionsEntities.SHADE_ALTAR_UPGRADE_HALO, ShadeAltarUpgradeHaloRenderer::new);
        event.register(CompanionsEntities.SHADE_SWORD_IMPACT_PROJECTILE, ShadeSwordImpactRenderer::new);
        event.register(CompanionsEntities.PONTIFF_FIRE_RING, PontiffFireRingRenderer::new);
        event.register(CompanionsEntities.SHADE_MAW_LANDING_RING, ShadeMawLandingRingRenderer::new);
        event.register(CompanionsEntities.NEEDLE_PROJECTILE, NeedleRenderer::new);
        event.register(CompanionsEntities.BLUE_ORB_PROJECTILE, BlueOrbRenderer::new);
        event.register(CompanionsEntities.HOLY_RING_PROJECTILE, HolyRingRenderer::new);
        event.register(CompanionsEntities.RESPAWN_TOTEM_RING_PROJECTILE, RespawnTotemRingRenderer::new);
        event.register(CompanionsEntities.LASER_PROJECTILE, LaserRenderer::new);
        event.register(CompanionsEntities.LASER_RING, LaserRingRenderer::new);
        event.register(CompanionsEntities.SCROLL, ScrollRenderer::new);
        event.register(CompanionsEntities.FROG_HEAL_PROJECTILE, FrogHealRenderer::new);
        event.register(CompanionsEntities.RED_STAR_EXPLOSION, RedStarExplosionRenderer::new);
        event.register(CompanionsEntities.RED_STAR_EXPLOSION_CENTER, RedStarExplosionCenterRenderer::new);
        event.register(CompanionsEntities.BLUE_STAR_EXPLOSION, BlueStarExplosionRenderer::new);
        event.register(CompanionsEntities.BLUE_STAR_EXPLOSION_CENTER, BlueStarExplosionCenterRenderer::new);
        event.register(CompanionsEntities.FROG_LEVITATE_PROJECTILE, FrogLevitateRenderer::new);
        event.register(CompanionsEntities.FROG_EGG_PROJECTILE, FrogEggRenderer::new);
        event.register(CompanionsEntities.ANTLION_SAND_PROJECTILE, AntlionSandProjectileRenderer::new);
        event.register(CompanionsEntities.BLOOD_SLASH_PROJECTILE, BloodSlashRenderer::new);
        event.register(CompanionsEntities.FIRE_GEISER_PROJECTILE, FireGeiserProjectileRenderer::new);
        event.register(CompanionsEntities.BONANZA_ANVIL, BonanzaAnvilRenderer::new);

        event.register(CompanionsEntities.GENERIC_TRIGGER_PROJECTILE, GenericTriggerProjectileRenderer::new);
        event.register(CompanionsEntities.CAKE_CREAM_TRIGGER_PROJECTILE, CakeCreamTriggerProjectileRenderer::new);
        event.register(CompanionsEntities.FIRE_RAY_BEAM_ENTITY, FireRayBeamRenderer::new);
    }

    @RegisterEvent
    public static void registerBlockEntityRenderers(final BlockEntityRendererRegistrationEvent event) {
        event.register(CompanionsBlockEntities.TESLA_COIL, TeslaCoilRenderer::new);
        event.register(CompanionsBlockEntities.SOUL_FURNACE, SoulFurnaceRenderer::new);
        event.register(CompanionsBlockEntities.CROISSANT_EGG, CroissantEggRenderer::new);
        event.register(CompanionsBlockEntities.PLASMA_LAMP, PlasmaLampRenderer::new);
        event.register(CompanionsBlockEntities.VOLTAIC_PILLAR, VoltaicPillarRenderer::new);
        event.register(CompanionsBlockEntities.EMPTY_PUPPET, EmptyPuppetRenderer::new);
        event.register(CompanionsBlockEntities.PORCELAIN_POTTERY, PorcelainPotteryRenderer::new);
        event.register(CompanionsBlockEntities.HOLY_PORCELAIN_POTTERY, HolyPorcelainPotteryRenderer::new);
        event.register(CompanionsBlockEntities.RESPAWN_TOTEM, RespawnTotemRenderer::new);
        event.register(CompanionsBlockEntities.SHADE_SWORD_ALTAR, ShadeSwordAltarRenderer::new);
        event.register(CompanionsBlockEntities.SHADE_MAW_ALTAR, ShadeMawAltarRenderer::new);
        event.register(CompanionsBlockEntities.SHADE_BAT_ALTAR, ShadeBatAltarRenderer::new);
        event.register(CompanionsBlockEntities.RECALL_PLATFORM, RecallPlatformRenderer::new);
        event.register(CompanionsBlockEntities.VOLTAIC_RELAY, VoltaicRelayRenderer::new);
        event.register(CompanionsBlockEntities.FROG_BONANZA, FrogBonanzaRenderer::new);
    }

    @RegisterEvent
    public static void registerMenuScreenRenderers(final MenuScreenRegistrationEvent event) {
        event.register(CompanionsMenuTypes.SOUL_FURNACE_MENU, SoulFurnaceScreen::new);
        event.register(CompanionsMenuTypes.SOUL_MAGE_MENU, SoulMageScreen::new);
        event.register(CompanionsMenuTypes.PUPPET_MENU, PuppetScreen::new);
        event.register(CompanionsMenuTypes.CORNELIUS_MENU, CorneliusScreen::new);
    }

    @RegisterEvent
    public static void registerBlockRenderTypes(final RenderLayerRegistrationEvent event) {
        event.setCutout(CompanionsBlocks.ETERNAL_FIRE.get());
    }

    @RegisterEvent
    public static void registerParticleProviders(final ParticleProviderRegistrationEvent event) {
        event.register(CompanionsParticles.TEDDY_TRANSFORMATION.get(), TeddyTransformationParticle.Provider::new);
        event.register(CompanionsParticles.TEDDY_TRANSFORMATION_CLOUD.get(), TeddyTransformationCloudParticle.Provider::new);
        event.register(CompanionsParticles.ILLAGER_GOLEM_SPARK.get(), IllagerGolemSparkParticle.Provider::new);
        event.register(CompanionsParticles.DINAMO_SPARK.get(), IllagerGolemSparkParticle.Provider::new);
        event.register(CompanionsParticles.EMBER.get(), EmberParticle.Provider::new);
        event.register(CompanionsParticles.BLACK_HOLE_STAR.get(), BlackHoleStarParticle.Provider::new);
        event.register(CompanionsParticles.BLIZZARD_SNOW.get(), BlizzardSnowParticle.Provider::new);
        event.register(CompanionsParticles.BLIZZARD_ICE.get(), BlizzardIceParticle.Provider::new);
        event.register(CompanionsParticles.GOLDEN_ALLAY_TRAIL.get(), GoldenAllayTrailParticle.Provider::new);
        event.register(CompanionsParticles.CAKE_CREAM.get(), CakeCreamParticle.Provider::new);
        event.register(CompanionsParticles.CAKE_CREAM_STRAWBERRY.get(), CakeCreamParticle.Provider::new);
        event.register(CompanionsParticles.CAKE_CREAM_CHOCOLATE.get(), CakeCreamParticle.Provider::new);
        event.register(CompanionsParticles.SOUL_FLAME.get(), SoulFlameParticle.Provider::new);
        event.register(CompanionsParticles.FIREWORK_TOAD.get(), FireworkToadParticle.Provider::new);
        event.register(CompanionsParticles.SHADE_TRAIL.get(), ShadeTrailParticle.Provider::new);
        event.register(CompanionsParticles.SHADE_SUMMON.get(), ShadeSummonParticle.Provider::new);
        event.register(CompanionsParticles.HOLINESS_RED_STAR_TRAIL.get(), GoldenAllayTrailParticle.Provider::new);
        event.register(CompanionsParticles.HOLINESS_BLUE_STAR_TRAIL.get(), GoldenAllayTrailParticle.Provider::new);
        event.register(CompanionsParticles.RESPAWN_TOTEM.get(), GoldenAllayTrailParticle.Provider::new);
        event.register(CompanionsParticles.BLINK.get(), BlinkParticle.Provider::new);
        event.register(CompanionsParticles.LASER_SPARK.get(), IllagerGolemSparkParticle.Provider::new);
        event.register(CompanionsParticles.EMBER_POLE_EXPLOSION.get(), TeddyTransformationParticle.Provider::new);
    }

    @RegisterEvent
    public static void registerAdditionalModels(final AdditionalModelsRegistrationEvent event) {
        event.register(new ModelResourceLocation(Companions.MOD_ID, "crystallized_blood_axe_icon", "inventory"));
        event.register(new ModelResourceLocation(Companions.MOD_ID, "crystallized_blood_scythe_icon", "inventory"));
        event.register(new ModelResourceLocation(Companions.MOD_ID, "crystallized_blood_sword_icon", "inventory"));
        event.register(new ModelResourceLocation(Companions.MOD_ID, "mage_staff_icon", "inventory"));
    }

    @RegisterEvent
    public static void registerBossBars(final BossBarRegistrationEvent event) {
        // Pontiff
        BossBarBuilder
                .matcher(context -> context.entity() instanceof SacredPontiffEntity)
                .renderer((gui, context, x, y) -> {
                    // background
                    final LerpingBossEvent bossEvent = context.event();
                    if (context.entity() instanceof SacredPontiffEntity pontiff) {
                        final int pontiffState = pontiff.getState();
                        // First phase
                        if (pontiffState == 1 || pontiffState == 2 || pontiffState == 3) {
                            gui.blit(PONTIFF_BACKGROUND, x, y + 22, 0, 0, 183, 5);

                            // health
                            final int healthWidth = (int) (bossEvent.getProgress() * 183);
                            if (healthWidth > 0) {
                                gui.blit(PONTIFF_HEALTH, x, y, 0, 0, healthWidth, 27);
                            }

                            // overlay
                            gui.blit(PONTIFF_OVERLAY, x - 3, y, 0, 0, 189, 30);

                            final int text = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - Minecraft.getInstance().font.width(bossEvent.getName()) / 2;
                            // name
                            gui.drawString(Minecraft.getInstance().font, bossEvent.getName(), text, y, 0xFFFFFF);
                        }
                        // Second phase
                        else if (pontiffState > 4) {
                            // background
                            gui.blit(HOLINESS_BACKGROUND, x - 2, y + 15, 0, 0, 185, 5);

                            // health
                            final int healthWidth = (int) (bossEvent.getProgress() * 185);
                            if (healthWidth > 0) {
                                gui.blit(HOLINESS_HEALTH, x - 2, y + 15, 0, 0, healthWidth, 28);
                            }

                            // overlay
                            gui.blit(HOLINESS_OVERLAY, x - 23, y, 0, 0, 227, 42);

                            final int text = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - Minecraft.getInstance().font.width(bossEvent.getName()) / 2;
                            gui.drawString(Minecraft.getInstance().font, bossEvent.getName(), text, y, 0xFFFFFF);
                        }

                    }

                })
                .padding(24)
                .hideVanillaName()
                .register();

    }

}
