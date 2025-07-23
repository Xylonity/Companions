package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.companion.*;
import dev.xylonity.companions.common.entity.hostile.*;
import dev.xylonity.companions.common.entity.projectile.*;
import dev.xylonity.companions.common.entity.projectile.trigger.CakeCreamTriggerProjectile;
import dev.xylonity.companions.common.entity.projectile.trigger.FireRayBeamEntity;
import dev.xylonity.companions.common.entity.projectile.trigger.GenericTriggerProjectile;
import dev.xylonity.companions.common.entity.projectile.trigger.LaserTriggerProjectile;
import dev.xylonity.companions.common.entity.summon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class CompanionsEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Companions.MOD_ID);

    public static final RegistryObject<EntityType<CorneliusEntity>> CORNELIUS;
    public static final RegistryObject<EntityType<TeddyEntity>> TEDDY;
    public static final RegistryObject<EntityType<AntlionEntity>> ANTLION;
    public static final RegistryObject<EntityType<DinamoEntity>> DINAMO;
    public static final RegistryObject<EntityType<BrokenDinamoEntity>> BROKEN_DINAMO;
    public static final RegistryObject<EntityType<HostileImpEntity>> HOSTILE_IMP;
    public static final RegistryObject<EntityType<MinionEntity>> MINION;
    public static final RegistryObject<EntityType<GoldenAllayEntity>> GOLDEN_ALLAY;
    public static final RegistryObject<EntityType<SoulMageEntity>> SOUL_MAGE;
    public static final RegistryObject<EntityType<LivingCandleEntity>> LIVING_CANDLE;
    public static final RegistryObject<EntityType<CroissantDragonEntity>> CROISSANT_DRAGON;
    public static final RegistryObject<EntityType<PuppetGloveEntity>> PUPPET_GLOVE;
    public static final RegistryObject<EntityType<PuppetEntity>> PUPPET;
    public static final RegistryObject<EntityType<ShadeSwordEntity>> SHADE_SWORD;
    public static final RegistryObject<EntityType<ShadeMawEntity>> SHADE_MAW;
    public static final RegistryObject<EntityType<MankhEntity>> MANKH;
    public static final RegistryObject<EntityType<CloakEntity>> CLOAK;

    public static final RegistryObject<EntityType<FireworkToadEntity>> FIREWORK_TOAD;
    public static final RegistryObject<EntityType<BubbleFrogEntity>> BUBBLE_FROG;
    public static final RegistryObject<EntityType<EmberPoleEntity>> EMBER_POLE;
    public static final RegistryObject<EntityType<NetherBullfrogEntity>> NETHER_BULLFROG;
    public static final RegistryObject<EntityType<EnderFrogEntity>> ENDER_FROG;

    public static final RegistryObject<EntityType<IllagerGolemEntity>> ILLAGER_GOLEM;
    public static final RegistryObject<EntityType<HostilePuppetGloveEntity>> HOSTILE_PUPPET_GLOVE;
    public static final RegistryObject<EntityType<SacredPontiffEntity>> SACRED_PONTIFF;
    public static final RegistryObject<EntityType<WildAntlionEntity>> WILD_ANTLION;

    public static final RegistryObject<EntityType<SmallIceShardProjectile>> SMALL_ICE_SHARD_PROJECTILE;
    public static final RegistryObject<EntityType<BigIceShardProjectile>> BIG_ICE_SHARD_PROJECTILE;
    public static final RegistryObject<EntityType<TornadoProjectile>> TORNADO_PROJECTILE;
    public static final RegistryObject<EntityType<BloodTornadoProjectile>> BLOOD_TORNADO_PROJECTILE;
    public static final RegistryObject<EntityType<BloodSlashProjectile>> BLOOD_SLASH_PROJECTILE;
    public static final RegistryObject<EntityType<FireMarkProjectile>> FIRE_MARK_PROJECTILE;
    public static final RegistryObject<EntityType<FireMarkRingProjectile>> FIRE_MARK_RING_PROJECTILE;
    public static final RegistryObject<EntityType<StoneSpikeProjectile>> STONE_SPIKE_PROJECTILE;
    public static final RegistryObject<EntityType<HealRingProjectile>> HEAL_RING_PROJECTILE;
    public static final RegistryObject<EntityType<BraceProjectile>> BRACE_PROJECTILE;
    public static final RegistryObject<EntityType<MagicRayPieceProjectile>> MAGIC_RAY_PIECE_PROJECTILE;
    public static final RegistryObject<EntityType<FireRayPieceProjectile>> FIRE_RAY_PIECE_PROJECTILE;
    public static final RegistryObject<EntityType<MagicRayCircleProjectile>> MAGIC_RAY_PIECE_CIRCLE_PROJECTILE;
    public static final RegistryObject<EntityType<BlackHoleProjectile>> BLACK_HOLE_PROJECTILE;
    public static final RegistryObject<EntityType<SoulMageBookEntity>> SOUL_MAGE_BOOK;
    public static final RegistryObject<EntityType<FloorCakeCreamProjectile>> FLOOR_CAKE_CREAM;
    public static final RegistryObject<EntityType<StakeProjectile>> STAKE_PROJECTILE;
    public static final RegistryObject<EntityType<HolinessNaginataProjectile>> HOLINESS_NAGINATA;
    public static final RegistryObject<EntityType<HolinessStartProjectile>> HOLINESS_STAR;
    public static final RegistryObject<EntityType<PontiffFireRingProjectile>> PONTIFF_FIRE_RING;
    public static final RegistryObject<EntityType<ShadeSwordImpactProjectile>> SHADE_SWORD_IMPACT_PROJECTILE;
    public static final RegistryObject<EntityType<NeedleProjectile>> NEEDLE_PROJECTILE;
    public static final RegistryObject<EntityType<LaserTriggerProjectile>> LASER_PROJECTILE;
    public static final RegistryObject<EntityType<LaserRingProjectile>> LASER_RING;
    public static final RegistryObject<EntityType<FrogHealProjectile>> FROG_HEAL_PROJECTILE;
    public static final RegistryObject<EntityType<FrogLevitateProjectile>> FROG_LEVITATE_PROJECTILE;
    public static final RegistryObject<EntityType<FrogEggProjectile>> FROG_EGG_PROJECTILE;
    public static final RegistryObject<EntityType<AntlionSandProjectile>> ANTLION_SAND_PROJECTILE;
    public static final RegistryObject<EntityType<FireGeiserProjectile>> FIRE_GEISER_PROJECTILE;

    public static final RegistryObject<EntityType<ScrollProjectile>> SCROLL;
    public static final RegistryObject<EntityType<RedStarExplosion>> RED_STAR_EXPLOSION;
    public static final RegistryObject<EntityType<RedStarExplosionCenter>> RED_STAR_EXPLOSION_CENTER;
    public static final RegistryObject<EntityType<BlueStarExplosion>> BLUE_STAR_EXPLOSION;
    public static final RegistryObject<EntityType<BlueStarExplosionCenter>> BLUE_STAR_EXPLOSION_CENTER;
    public static final RegistryObject<EntityType<ShadeAltarUpgradeHaloProjectile>> SHADE_ALTAR_UPGRADE_HALO;
    public static final RegistryObject<EntityType<RespawnTotemRingProjectile>> RESPAWN_TOTEM_RING_PROJECTILE;

    public static final RegistryObject<EntityType<GenericTriggerProjectile>> GENERIC_TRIGGER_PROJECTILE;
    public static final RegistryObject<EntityType<CakeCreamTriggerProjectile>> CAKE_CREAM_TRIGGER_PROJECTILE;
    public static final RegistryObject<EntityType<FireRayBeamEntity>> FIRE_RAY_BEAM_ENTITY;

    static {
        CORNELIUS = register("cornelius", CorneliusEntity::new, MobCategory.CREATURE, 1f, 1f, null);
        TEDDY = register("teddy", TeddyEntity::new, MobCategory.CREATURE, 0.9f, 0.9f, null);
        ANTLION = register("antlion", AntlionEntity::new, MobCategory.CREATURE, 1f, 1f, null);
        DINAMO = register("dinamo", DinamoEntity::new, MobCategory.CREATURE, 1f, 2f, null);
        BROKEN_DINAMO = register("broken_dinamo", BrokenDinamoEntity::new, MobCategory.CREATURE, 1f, 0.5f, null);
        HOSTILE_IMP = register("hostile_imp", HostileImpEntity::new, MobCategory.MONSTER, 0.85f, 1.5f, null);
        MINION = register("minion", MinionEntity::new, MobCategory.CREATURE, 0.85f, 1.5f, null);
        GOLDEN_ALLAY = register("golden_allay", GoldenAllayEntity::new, MobCategory.CREATURE, 0.5f, 0.85f, null);
        SOUL_MAGE = register("soul_mage", SoulMageEntity::new, MobCategory.CREATURE, 0.85f, 1.2f, null);
        CROISSANT_DRAGON = register("croissant_dragon", CroissantDragonEntity::new, MobCategory.CREATURE, 1.4f, 1.4f, null);
        PUPPET_GLOVE = register("puppet_glove", PuppetGloveEntity::new, MobCategory.CREATURE, 0.8f, 0.8f, null);
        PUPPET = register("puppet", PuppetEntity::new, MobCategory.CREATURE, 1f, 2.8f, null);
        SHADE_SWORD = register("shade_sword", ShadeSwordEntity::new, MobCategory.CREATURE, 1.25f, 4f, List.of(EntityType.Builder::fireImmune));
        SHADE_MAW = register("shade_maw", ShadeMawEntity::new, MobCategory.CREATURE, 3f, 2.5f, List.of(EntityType.Builder::fireImmune));
        MANKH = register("mankh", MankhEntity::new, MobCategory.CREATURE, 1f, 2f, List.of(EntityType.Builder::fireImmune));
        CLOAK = register("cloak", CloakEntity::new, MobCategory.CREATURE, 1f, 2f, List.of(EntityType.Builder::fireImmune));

        LIVING_CANDLE = register("living_candle", LivingCandleEntity::new, MobCategory.CREATURE, 0.5f, 0.75f, null);
        FIREWORK_TOAD = register("firework_toad", FireworkToadEntity::new, MobCategory.CREATURE, 1f, 1f, null);
        BUBBLE_FROG = register("bubble_frog", BubbleFrogEntity::new, MobCategory.CREATURE, 1f, 1f, null);
        EMBER_POLE = register("ember_pole", EmberPoleEntity::new, MobCategory.CREATURE, 1f, 1f, List.of(EntityType.Builder::fireImmune));
        NETHER_BULLFROG = register("nether_bullfrog", NetherBullfrogEntity::new, MobCategory.CREATURE, 1f, 1f, List.of(EntityType.Builder::fireImmune));
        ENDER_FROG = register("ender_frog", EnderFrogEntity::new, MobCategory.CREATURE, 1f, 2f, null);

        ILLAGER_GOLEM = register("illager_golem", IllagerGolemEntity::new, MobCategory.MONSTER, 1f, 2f, null);
        HOSTILE_PUPPET_GLOVE = register("hostile_puppet_glove", HostilePuppetGloveEntity::new, MobCategory.CREATURE, 0.8f, 2f, null);
        SACRED_PONTIFF = register("sacred_pontiff", SacredPontiffEntity::new, MobCategory.CREATURE, 2f, 4f, List.of(EntityType.Builder::fireImmune));
        WILD_ANTLION = register("wild_antlion", WildAntlionEntity::new, MobCategory.MONSTER, 1f, 1f, null);

        SMALL_ICE_SHARD_PROJECTILE = register("small_ice_shard_projectile", SmallIceShardProjectile::new, MobCategory.MISC, 0.4f, 0.5f, List.of(EntityType.Builder::noSummon));
        BIG_ICE_SHARD_PROJECTILE = register("big_ice_shard_projectile", BigIceShardProjectile::new, MobCategory.MISC, 1f, 0.5f, List.of(EntityType.Builder::noSummon));
        TORNADO_PROJECTILE = register("tornado_projectile", TornadoProjectile::new, MobCategory.MISC, 0.8f, 1f, List.of(EntityType.Builder::noSummon));
        BLOOD_TORNADO_PROJECTILE = register("blood_tornado_projectile", BloodTornadoProjectile::new, MobCategory.MISC, 0.8f, 1f, List.of(EntityType.Builder::noSummon));
        BLOOD_SLASH_PROJECTILE = register("blood_slash_projectile", BloodSlashProjectile::new, MobCategory.MISC, 0.8f, 1f, List.of(EntityType.Builder::noSummon));
        FIRE_MARK_PROJECTILE = register("fire_mark_projectile", FireMarkProjectile::new, MobCategory.MISC, 1f, 1f, List.of(EntityType.Builder::noSummon));
        FIRE_MARK_RING_PROJECTILE = register("fire_mark_ring_projectile", FireMarkRingProjectile::new, MobCategory.MISC, 1f, 0.2f, List.of(EntityType.Builder::noSummon));
        STONE_SPIKE_PROJECTILE = register("stone_spike_projectile", StoneSpikeProjectile::new, MobCategory.MISC, 0.5f, 1f, List.of(EntityType.Builder::noSummon));
        HEAL_RING_PROJECTILE = register("heal_ring_projectile", HealRingProjectile::new, MobCategory.MISC, 0.5f, 1f, List.of(EntityType.Builder::noSummon));
        BRACE_PROJECTILE = register("brace_projectile", BraceProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon, EntityType.Builder::fireImmune));
        MAGIC_RAY_PIECE_PROJECTILE = register("magic_ray_piece_projectile", MagicRayPieceProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
        FIRE_RAY_PIECE_PROJECTILE = register("fire_ray_piece_projectile", FireRayPieceProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
        MAGIC_RAY_PIECE_CIRCLE_PROJECTILE = register("magic_ray_circle_projectile", MagicRayCircleProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
        BLACK_HOLE_PROJECTILE = register("black_hole_projectile", BlackHoleProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
        SOUL_MAGE_BOOK = register("soul_mage_book", SoulMageBookEntity::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
        FLOOR_CAKE_CREAM = register("floor_cake_cream_projectile", FloorCakeCreamProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
        STAKE_PROJECTILE = register("stake_projectile", StakeProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
        HOLINESS_NAGINATA = register("holiness_naginata", HolinessNaginataProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
        HOLINESS_STAR = register("holiness_star", HolinessStartProjectile::new, MobCategory.MISC, 0.15f, 0.15f, List.of(EntityType.Builder::noSummon));
        PONTIFF_FIRE_RING = register("pontiff_fire_ring", PontiffFireRingProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
        SHADE_SWORD_IMPACT_PROJECTILE = register("shade_sword_impact", ShadeSwordImpactProjectile::new, MobCategory.MISC, 1.2f, 1.2f, List.of(EntityType.Builder::noSummon));
        NEEDLE_PROJECTILE = register("needle_projectile", NeedleProjectile::new, MobCategory.MISC, 1.2f, 0.4f, List.of(EntityType.Builder::noSummon));
        LASER_PROJECTILE = register("laser", LaserTriggerProjectile::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
        LASER_RING = register("laser_ring", LaserRingProjectile::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
        FROG_HEAL_PROJECTILE = register("frog_heal_projectile", FrogHealProjectile::new, MobCategory.MISC, 0.4f, 0.4f, List.of(EntityType.Builder::noSummon, e -> e.clientTrackingRange(8)));
        FROG_LEVITATE_PROJECTILE = register("frog_levitate_projectile", FrogLevitateProjectile::new, MobCategory.MISC, 0.4f, 0.4f, List.of(EntityType.Builder::noSummon, e -> e.clientTrackingRange(8)));
        FROG_EGG_PROJECTILE = register("frog_egg_projectile", FrogEggProjectile::new, MobCategory.MISC, 0.4f, 0.4f, List.of(EntityType.Builder::noSummon, e -> e.clientTrackingRange(8)));
        ANTLION_SAND_PROJECTILE = register("antlion_sand_projectile", AntlionSandProjectile::new, MobCategory.MISC, 0.4f, 0.4f, List.of(EntityType.Builder::noSummon, e -> e.clientTrackingRange(8)));
        FIRE_GEISER_PROJECTILE = register("fire_geiser_projectile", FireGeiserProjectile::new, MobCategory.MISC, 1f, 0.4f, List.of(EntityType.Builder::noSummon, e -> e.clientTrackingRange(8)));

        SCROLL = register("scroll", ScrollProjectile::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
        RESPAWN_TOTEM_RING_PROJECTILE = register("respawn_totem_ring_projectile", RespawnTotemRingProjectile::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
        SHADE_ALTAR_UPGRADE_HALO = register("shade_altar_upgrade_halo", ShadeAltarUpgradeHaloProjectile::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
        RED_STAR_EXPLOSION = register("red_star_explosion", RedStarExplosion::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
        RED_STAR_EXPLOSION_CENTER = register("red_star_explosion_center", RedStarExplosionCenter::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
        BLUE_STAR_EXPLOSION = register("blue_star_explosion", BlueStarExplosion::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));
        BLUE_STAR_EXPLOSION_CENTER = register("blue_star_explosion_center", BlueStarExplosionCenter::new, MobCategory.MISC, 0.1f, 0.1f, List.of(EntityType.Builder::noSummon));

        GENERIC_TRIGGER_PROJECTILE = register("generic_trigger_projectile", GenericTriggerProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
        CAKE_CREAM_TRIGGER_PROJECTILE = register("cake_cream_trigger_projectile", CakeCreamTriggerProjectile::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
        FIRE_RAY_BEAM_ENTITY = register("fire_ray_beam_entity", FireRayBeamEntity::new, MobCategory.MISC, 0.6f, 0.6f, List.of(EntityType.Builder::noSummon));
    }

    private static <X extends Entity> RegistryObject<EntityType<X>> register(String name, EntityType.EntityFactory<X> entity, MobCategory category, float width, float height, @Nullable List<Consumer<EntityType.Builder<X>>> properties) {
        return ENTITY.register(name, () -> {
            EntityType.Builder<X> builder = EntityType.Builder.of(entity, category).sized(width, height);

            if (properties != null) {
                for (Consumer<EntityType.Builder<X>> property : properties) {
                    property.accept(builder);
                }
            }

            return builder.build(new ResourceLocation(Companions.MOD_ID, name).toString());
        });
    }

}
