package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class CompanionsSounds {

    public static final ResourceRegistry<SoundEvent> SOUNDS = ResourceDispatcher.create(BuiltInRegistries.SOUND_EVENT, Companions.MOD_ID);

    public static final Supplier<SoundEvent> SAINT_KLIMT = SOUNDS.register("saint_klimt", () -> SoundEvent.createVariableRangeEvent(Companions.of("saint_klimt")));
    public static final Supplier<SoundEvent> FLIP_CARD = SOUNDS.register("flip_card", () -> SoundEvent.createVariableRangeEvent(Companions.of("flip_card")));
    public static final Supplier<SoundEvent> BONANZA = SOUNDS.register("bonanza", () -> SoundEvent.createVariableRangeEvent(Companions.of("bonanza")));
    public static final Supplier<SoundEvent> COIN_CLATTER = SOUNDS.register("coin_clatter", () -> SoundEvent.createVariableRangeEvent(Companions.of("coin_clatter")));
    public static final Supplier<SoundEvent> WRENCH_CONNECTION = SOUNDS.register("wrench_connection", () -> SoundEvent.createVariableRangeEvent(Companions.of("wrench_connection")));
    public static final Supplier<SoundEvent> POP = SOUNDS.register("pop", () -> SoundEvent.createVariableRangeEvent(Companions.of("pop")));
    public static final Supplier<SoundEvent> STAR_EXPLOSION = SOUNDS.register("star_explosion", () -> SoundEvent.createVariableRangeEvent(Companions.of("star_explosion")));
    public static final Supplier<SoundEvent> SCROLL_SOUND = SOUNDS.register("scroll_sound", () -> SoundEvent.createVariableRangeEvent(Companions.of("scroll_sound")));
    public static final Supplier<SoundEvent> SHADE_ALTAR_CHARGE = SOUNDS.register("shade_altar_charge", () -> SoundEvent.createVariableRangeEvent(Companions.of("shade_altar_charge")));
    public static final Supplier<SoundEvent> SHADE_ALTAR_FULL = SOUNDS.register("shade_altar_full", () -> SoundEvent.createVariableRangeEvent(Companions.of("shade_altar_full")));

    public static final Supplier<SoundEvent> SPELL_HIT_MARK = SOUNDS.register("spell_hit_mark", () -> SoundEvent.createVariableRangeEvent(Companions.of("spell_hit_mark")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_BEAM = SOUNDS.register("spell_release_beam", () -> SoundEvent.createVariableRangeEvent(Companions.of("spell_release_beam")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_BRACE = SOUNDS.register("spell_release_brace", () -> SoundEvent.createVariableRangeEvent(Companions.of("spell_release_brace")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_DARK_HOLE = SOUNDS.register("spell_release_dark_hole", () -> SoundEvent.createVariableRangeEvent(Companions.of("spell_release_dark_hole")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_HEAL = SOUNDS.register("spell_release_heal", () -> SoundEvent.createVariableRangeEvent(Companions.of("spell_release_heal")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_ICE = SOUNDS.register("spell_release_ice", () -> SoundEvent.createVariableRangeEvent(Companions.of("spell_release_ice")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_MARK = SOUNDS.register("spell_release_mark", () -> SoundEvent.createVariableRangeEvent(Companions.of("spell_release_mark")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_SPEARS = SOUNDS.register("spell_release_spears", () -> SoundEvent.createVariableRangeEvent(Companions.of("spell_release_spears")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_TORNADO = SOUNDS.register("spell_release_tornado", () -> SoundEvent.createVariableRangeEvent(Companions.of("spell_release_tornado")));

    public static final Supplier<SoundEvent> DINAMO_STEP = SOUNDS.register("dinamo_step", () -> SoundEvent.createVariableRangeEvent(Companions.of("dinamo_step")));
    public static final Supplier<SoundEvent> DINAMO_IDLE = SOUNDS.register("dinamo_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("dinamo_idle")));
    public static final Supplier<SoundEvent> DINAMO_HURT = SOUNDS.register("dinamo_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("dinamo_hurt")));
    public static final Supplier<SoundEvent> DINAMO_DEATH = SOUNDS.register("dinamo_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("dinamo_death")));
    public static final Supplier<SoundEvent> DINAMO_ATTACK = SOUNDS.register("dinamo_attack", () -> SoundEvent.createVariableRangeEvent(Companions.of("dinamo_attack")));

    public static final Supplier<SoundEvent> MUTANT_TEDDY_ATTACK = SOUNDS.register("mutant_teddy_attack", () -> SoundEvent.createVariableRangeEvent(Companions.of("mutant_teddy_attack")));
    public static final Supplier<SoundEvent> MUTANT_TEDDY_FLAP_WINGS = SOUNDS.register("mutant_teddy_flap_wings", () -> SoundEvent.createVariableRangeEvent(Companions.of("mutant_teddy_flap_wings")));
    public static final Supplier<SoundEvent> MUTANT_TEDDY_HURT = SOUNDS.register("mutant_teddy_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("mutant_teddy_hurt")));
    public static final Supplier<SoundEvent> MUTANT_TEDDY_IDLE = SOUNDS.register("mutant_teddy_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("mutant_teddy_idle")));
    public static final Supplier<SoundEvent> TEDDY_ATTACK = SOUNDS.register("teddy_attack", () -> SoundEvent.createVariableRangeEvent(Companions.of("teddy_attack")));
    public static final Supplier<SoundEvent> TEDDY_STEP = SOUNDS.register("teddy_step", () -> SoundEvent.createVariableRangeEvent(Companions.of("teddy_step")));
    public static final Supplier<SoundEvent> TEDDY_AUTO_STAB = SOUNDS.register("teddy_auto_stab", () -> SoundEvent.createVariableRangeEvent(Companions.of("teddy_auto_stab")));
    public static final Supplier<SoundEvent> TEDDY_TRANSFORMATION = SOUNDS.register("teddy_transformation", () -> SoundEvent.createVariableRangeEvent(Companions.of("teddy_transformation")));

    public static final Supplier<SoundEvent> ANTLION_DEATH = SOUNDS.register("antlion_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("antlion_death")));
    public static final Supplier<SoundEvent> ANTLION_HURT = SOUNDS.register("antlion_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("antlion_hurt")));
    public static final Supplier<SoundEvent> ANTLION_IDLE = SOUNDS.register("antlion_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("antlion_idle")));
    public static final Supplier<SoundEvent> ANTLION_STEPS = SOUNDS.register("antlion_steps", () -> SoundEvent.createVariableRangeEvent(Companions.of("antlion_steps")));
    public static final Supplier<SoundEvent> ADULT_ANTLION_DEATH = SOUNDS.register("dragonfly_antlion_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("dragonfly_antlion_death")));
    public static final Supplier<SoundEvent> ADULT_ANTLION_FLY = SOUNDS.register("dragonfly_antlion_fly", () -> SoundEvent.createVariableRangeEvent(Companions.of("dragonfly_antlion_fly")));
    public static final Supplier<SoundEvent> ADULT_ANTLION_HURT = SOUNDS.register("dragonfly_antlion_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("dragonfly_antlion_hurt")));
    public static final Supplier<SoundEvent> PUPA_ANTLION_STEP = SOUNDS.register("pupa_antlion_step", () -> SoundEvent.createVariableRangeEvent(Companions.of("pupa_antlion_step")));
    public static final Supplier<SoundEvent> SOLDIER_ANTLION_DEATH = SOUNDS.register("tank_antlion_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("tank_antlion_death")));
    public static final Supplier<SoundEvent> SOLDIER_ANTLION_HURT = SOUNDS.register("tank_antlion_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("tank_antlion_hurt")));
    public static final Supplier<SoundEvent> SOLDIER_ANTLION_IDLE = SOUNDS.register("tank_antlion_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("tank_antlion_idle")));
    public static final Supplier<SoundEvent> SOLDIER_ANTLION_SAND_CANNON = SOUNDS.register("tank_antlion_sand_cannon", () -> SoundEvent.createVariableRangeEvent(Companions.of("tank_antlion_sand_cannon")));
    public static final Supplier<SoundEvent> SOLDIER_ANTLION_STEP = SOUNDS.register("tank_antlion_sand_step", () -> SoundEvent.createVariableRangeEvent(Companions.of("tank_antlion_sand_step")));

    public static final Supplier<SoundEvent> CLOAK_DEATH = SOUNDS.register("cloak_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("cloak_death")));
    public static final Supplier<SoundEvent> CLOAK_HURT = SOUNDS.register("cloak_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("cloak_hurt")));
    public static final Supplier<SoundEvent> CLOAK_IDLE = SOUNDS.register("cloak_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("cloak_idle")));
    public static final Supplier<SoundEvent> CLOAK_STEP = SOUNDS.register("cloak_step", () -> SoundEvent.createVariableRangeEvent(Companions.of("cloak_step")));
    public static final Supplier<SoundEvent> MANKH_BEAM = SOUNDS.register("mankh_beam", () -> SoundEvent.createVariableRangeEvent(Companions.of("mankh_beam")));
    public static final Supplier<SoundEvent> MANKH_DEATH = SOUNDS.register("mankh_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("mankh_death")));
    public static final Supplier<SoundEvent> MANKH_RING = SOUNDS.register("mankh_ring", () -> SoundEvent.createVariableRangeEvent(Companions.of("mankh_ring")));
    public static final Supplier<SoundEvent> MANKH_STEP = SOUNDS.register("mankh_step", () -> SoundEvent.createVariableRangeEvent(Companions.of("mankh_step")));

    public static final Supplier<SoundEvent> HOLINESS_DEATH = SOUNDS.register("holiness_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("holiness_death")));
    public static final Supplier<SoundEvent> HOLINESS_FLY_OFF = SOUNDS.register("holiness_fly_off", () -> SoundEvent.createVariableRangeEvent(Companions.of("holiness_fly_off")));
    public static final Supplier<SoundEvent> HOLINESS_HIT_CHEST = SOUNDS.register("holiness_hit_chest", () -> SoundEvent.createVariableRangeEvent(Companions.of("holiness_hit_chest")));
    public static final Supplier<SoundEvent> HOLINESS_HIT_GROUND = SOUNDS.register("holiness_hit_ground", () -> SoundEvent.createVariableRangeEvent(Companions.of("holiness_hit_ground")));
    public static final Supplier<SoundEvent> HOLINESS_STAB = SOUNDS.register("holiness_stab", () -> SoundEvent.createVariableRangeEvent(Companions.of("holiness_stab")));
    public static final Supplier<SoundEvent> HOLINESS_STAKE = SOUNDS.register("holiness_stake", () -> SoundEvent.createVariableRangeEvent(Companions.of("holiness_stake")));
    public static final Supplier<SoundEvent> HOLINESS_STAR_SPAWN = SOUNDS.register("holiness_star_spawn", () -> SoundEvent.createVariableRangeEvent(Companions.of("holiness_star_spawn")));
    public static final Supplier<SoundEvent> HOLINESS_APPEAR = SOUNDS.register("holiness_appear", () -> SoundEvent.createVariableRangeEvent(Companions.of("holiness_appear")));
    public static final Supplier<SoundEvent> PONTIFF_ACTIVATE = SOUNDS.register("pontiff_activate", () -> SoundEvent.createVariableRangeEvent(Companions.of("pontiff_activate")));
    public static final Supplier<SoundEvent> PONTIFF_AREA_ATTACK = SOUNDS.register("pontiff_area_attack", () -> SoundEvent.createVariableRangeEvent(Companions.of("pontiff_area_attack")));
    public static final Supplier<SoundEvent> PONTIFF_FRONT_ATTACK = SOUNDS.register("pontiff_front_attack", () -> SoundEvent.createVariableRangeEvent(Companions.of("pontiff_front_attack")));
    public static final Supplier<SoundEvent> PONTIFF_GROUND_DESPAWN = SOUNDS.register("pontiff_ground_despawn", () -> SoundEvent.createVariableRangeEvent(Companions.of("pontiff_ground_despawn")));
    public static final Supplier<SoundEvent> PONTIFF_HURT = SOUNDS.register("pontiff_ground_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("pontiff_ground_hurt")));
    public static final Supplier<SoundEvent> PONTIFF_IDLE = SOUNDS.register("pontiff_ground_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("pontiff_ground_idle")));
    public static final Supplier<SoundEvent> PONTIFF_STEP = SOUNDS.register("pontiff_step", () -> SoundEvent.createVariableRangeEvent(Companions.of("pontiff_step")));
    public static final Supplier<SoundEvent> PONTIFF_DESPAWN = SOUNDS.register("pontiff_despawn", () -> SoundEvent.createVariableRangeEvent(Companions.of("pontiff_despawn")));

    public static final Supplier<SoundEvent> CROISSANT_DRAGON_BREATH = SOUNDS.register("croissant_dragon_breath", () -> SoundEvent.createVariableRangeEvent(Companions.of("croissant_dragon_breath")));
    public static final Supplier<SoundEvent> CROISSANT_DRAGON_CREAM_SPLAT = SOUNDS.register("croissant_dragon_cream_splat", () -> SoundEvent.createVariableRangeEvent(Companions.of("croissant_dragon_cream_splat")));
    public static final Supplier<SoundEvent> CROISSANT_DRAGON_CREAM_DEATH = SOUNDS.register("croissant_dragon_cream_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("croissant_dragon_cream_death")));
    public static final Supplier<SoundEvent> CROISSANT_DRAGON_CREAM_HURT = SOUNDS.register("croissant_dragon_cream_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("croissant_dragon_cream_hurt")));
    public static final Supplier<SoundEvent> CROISSANT_DRAGON_CREAM_IDLE = SOUNDS.register("croissant_dragon_cream_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("croissant_dragon_cream_idle")));
    public static final Supplier<SoundEvent> CROISSANT_DRAGON_CREAM_STEPS = SOUNDS.register("croissant_dragon_cream_steps", () -> SoundEvent.createVariableRangeEvent(Companions.of("croissant_dragon_cream_steps")));
    public static final Supplier<SoundEvent> CROISSANT_DRAGON_CREAM_WING_FLAP = SOUNDS.register("croissant_dragon_cream_wing_flap", () -> SoundEvent.createVariableRangeEvent(Companions.of("croissant_dragon_cream_wing_flap")));

    public static final Supplier<SoundEvent> END_FROG_HEAL = SOUNDS.register("end_frog_heal", () -> SoundEvent.createVariableRangeEvent(Companions.of("end_frog_heal")));
    public static final Supplier<SoundEvent> FROGGY_ATTACK = SOUNDS.register("froggy_attack", () -> SoundEvent.createVariableRangeEvent(Companions.of("froggy_attack")));
    public static final Supplier<SoundEvent> FROGGY_DEATH = SOUNDS.register("froggy_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("froggy_death")));
    public static final Supplier<SoundEvent> FROGGY_HURT = SOUNDS.register("froggy_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("froggy_hurt")));
    public static final Supplier<SoundEvent> FROGGY_IDLE = SOUNDS.register("froggy_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("froggy_idle")));
    public static final Supplier<SoundEvent> FROGGY_JUMP = SOUNDS.register("froggy_jump", () -> SoundEvent.createVariableRangeEvent(Companions.of("froggy_jump")));
    public static final Supplier<SoundEvent> MID_FROG_DEATH = SOUNDS.register("mid_frog_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("mid_frog_death")));
    public static final Supplier<SoundEvent> MID_FROG_IDLE = SOUNDS.register("mid_frog_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("mid_frog_idle")));
    public static final Supplier<SoundEvent> MID_FROG_JUMP = SOUNDS.register("mid_frog_jump", () -> SoundEvent.createVariableRangeEvent(Companions.of("mid_frog_jump")));
    public static final Supplier<SoundEvent> NETHER_BULLFROG_AIR_SLASH_END = SOUNDS.register("nether_bullfrog_air_slash_end", () -> SoundEvent.createVariableRangeEvent(Companions.of("nether_bullfrog_air_slash_end")));
    public static final Supplier<SoundEvent> NETHER_BULLFROG_DEATH = SOUNDS.register("nether_bullfrog_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("nether_bullfrog_death")));
    public static final Supplier<SoundEvent> NETHER_BULLFROG_HURT = SOUNDS.register("nether_bullfrog_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("nether_bullfrog_hurt")));
    public static final Supplier<SoundEvent> NETHER_BULLFROG_IDLE = SOUNDS.register("nether_bullfrog_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("nether_bullfrog_idle")));
    public static final Supplier<SoundEvent> NETHER_BULLFROG_JUMP = SOUNDS.register("nether_bullfrog_jump", () -> SoundEvent.createVariableRangeEvent(Companions.of("nether_bullfrog_jump")));
    public static final Supplier<SoundEvent> NETHER_BULLFROG_SLASH = SOUNDS.register("nether_bullfrog_slash", () -> SoundEvent.createVariableRangeEvent(Companions.of("nether_bullfrog_slash")));
    public static final Supplier<SoundEvent> SMALL_FROG_DEATH = SOUNDS.register("small_frog_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("small_frog_death")));
    public static final Supplier<SoundEvent> SMALL_FROG_HURT = SOUNDS.register("small_frog_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("small_frog_hurt")));
    public static final Supplier<SoundEvent> SMALL_FROG_IDLE = SOUNDS.register("small_frog_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("small_frog_idle")));
    public static final Supplier<SoundEvent> SMALL_FROG_JUMP = SOUNDS.register("small_frog_jump", () -> SoundEvent.createVariableRangeEvent(Companions.of("small_frog_jump")));
    public static final Supplier<SoundEvent> SMALL_FROG_SHOOT = SOUNDS.register("small_frog_shoot", () -> SoundEvent.createVariableRangeEvent(Companions.of("small_frog_shoot")));

    public static final Supplier<SoundEvent> MAGE_DEATH = SOUNDS.register("mage_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("mage_death")));
    public static final Supplier<SoundEvent> MAGE_GET_BOOK = SOUNDS.register("mage_get_book", () -> SoundEvent.createVariableRangeEvent(Companions.of("mage_get_book")));
    public static final Supplier<SoundEvent> MAGE_HURT = SOUNDS.register("mage_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("mage_hurt")));
    public static final Supplier<SoundEvent> MAGE_IDLE = SOUNDS.register("mage_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("mage_idle")));
    public static final Supplier<SoundEvent> MAGE_STEP = SOUNDS.register("mage_step", () -> SoundEvent.createVariableRangeEvent(Companions.of("mage_step")));

    public static final Supplier<SoundEvent> END_MINION_DEATH = SOUNDS.register("end_minion_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("end_minion_death")));
    public static final Supplier<SoundEvent> END_MINION_FLAP = SOUNDS.register("end_minion_flap", () -> SoundEvent.createVariableRangeEvent(Companions.of("end_minion_flap")));
    public static final Supplier<SoundEvent> END_MINION_HURT = SOUNDS.register("end_minion_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("end_minion_hurt")));
    public static final Supplier<SoundEvent> END_MINION_STEP = SOUNDS.register("end_minion_step", () -> SoundEvent.createVariableRangeEvent(Companions.of("end_minion_step")));
    public static final Supplier<SoundEvent> NETHER_MINION_DEATH = SOUNDS.register("nether_minion_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("nether_minion_death")));
    public static final Supplier<SoundEvent> NETHER_MINION_HURT = SOUNDS.register("nether_minion_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("nether_minion_hurt")));
    public static final Supplier<SoundEvent> NETHER_MINION_IDLE = SOUNDS.register("nether_minion_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("nether_minion_idle")));
    public static final Supplier<SoundEvent> OVERWORLD_MINION_DEATH = SOUNDS.register("overworld_minion_death", () -> SoundEvent.createVariableRangeEvent(Companions.of("overworld_minion_death")));
    public static final Supplier<SoundEvent> OVERWORLD_MINION_HURT = SOUNDS.register("overworld_minion_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("overworld_minion_hurt")));
    public static final Supplier<SoundEvent> OVERWORLD_MINION_IDLE = SOUNDS.register("overworld_minion_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("overworld_minion_idle")));

    public static final Supplier<SoundEvent> HOSTILE_PUPPET_GLOVE_ATTACK = SOUNDS.register("hostile_puppet_glove_attack", () -> SoundEvent.createVariableRangeEvent(Companions.of("hostile_puppet_glove_attack")));
    public static final Supplier<SoundEvent> HOSTILE_PUPPET_GLOVE_CLEAN = SOUNDS.register("hostile_puppet_glove_clean", () -> SoundEvent.createVariableRangeEvent(Companions.of("hostile_puppet_glove_clean")));
    public static final Supplier<SoundEvent> HOSTILE_PUPPET_GLOVE_LOOSE = SOUNDS.register("hostile_puppet_glove_loose", () -> SoundEvent.createVariableRangeEvent(Companions.of("hostile_puppet_glove_loose")));
    public static final Supplier<SoundEvent> PUPPET_ATTACK_BLADE = SOUNDS.register("puppet_attack_blade", () -> SoundEvent.createVariableRangeEvent(Companions.of("puppet_attack_blade")));
    public static final Supplier<SoundEvent> PUPPET_ATTACK_CANON = SOUNDS.register("puppet_attack_canon", () -> SoundEvent.createVariableRangeEvent(Companions.of("puppet_attack_canon")));
    public static final Supplier<SoundEvent> PUPPET_ATTACK_MUTANT = SOUNDS.register("puppet_attack_mutant", () -> SoundEvent.createVariableRangeEvent(Companions.of("puppet_attack_mutant")));
    public static final Supplier<SoundEvent> PUPPET_ATTACK_WHIP = SOUNDS.register("puppet_attack_whip", () -> SoundEvent.createVariableRangeEvent(Companions.of("puppet_attack_whip")));
    public static final Supplier<SoundEvent> PUPPET_EQUIP_BLADE = SOUNDS.register("puppet_equip_blade", () -> SoundEvent.createVariableRangeEvent(Companions.of("puppet_equip_blade")));
    public static final Supplier<SoundEvent> PUPPET_EQUIP_CANON = SOUNDS.register("puppet_equip_canon", () -> SoundEvent.createVariableRangeEvent(Companions.of("puppet_equip_canon")));
    public static final Supplier<SoundEvent> PUPPET_EQUIP_MUTANT = SOUNDS.register("puppet_equip_mutant", () -> SoundEvent.createVariableRangeEvent(Companions.of("puppet_equip_mutant")));
    public static final Supplier<SoundEvent> PUPPET_EQUIP_WHIP = SOUNDS.register("puppet_equip_whip", () -> SoundEvent.createVariableRangeEvent(Companions.of("puppet_equip_whip")));
    public static final Supplier<SoundEvent> PUPPET_GLOVE_SHOOT = SOUNDS.register("puppet_glove_shoot", () -> SoundEvent.createVariableRangeEvent(Companions.of("puppet_glove_shoot")));
    public static final Supplier<SoundEvent> PUPPET_GLOVE_STEP = SOUNDS.register("puppet_glove_step", () -> SoundEvent.createVariableRangeEvent(Companions.of("puppet_glove_step")));
    public static final Supplier<SoundEvent> PUPPET_HURT = SOUNDS.register("puppet_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("puppet_hurt")));
    public static final Supplier<SoundEvent> PUPPET_WALK = SOUNDS.register("puppet_walk", () -> SoundEvent.createVariableRangeEvent(Companions.of("puppet_walk")));

    public static final Supplier<SoundEvent> SHADE_BELL_SUMMON = SOUNDS.register("shade_bell_summon", () -> SoundEvent.createVariableRangeEvent(Companions.of("shade_bell_summon")));
    public static final Supplier<SoundEvent> SHADE_DESPAWN = SOUNDS.register("shade_despawn", () -> SoundEvent.createVariableRangeEvent(Companions.of("shade_despawn")));
    public static final Supplier<SoundEvent> SHADE_HURT = SOUNDS.register("shade_hurt", () -> SoundEvent.createVariableRangeEvent(Companions.of("shade_hurt")));
    public static final Supplier<SoundEvent> SHADE_IDLE = SOUNDS.register("shade_idle", () -> SoundEvent.createVariableRangeEvent(Companions.of("shade_idle")));
    public static final Supplier<SoundEvent> SHADE_MAW_BITE = SOUNDS.register("shade_maw_bite", () -> SoundEvent.createVariableRangeEvent(Companions.of("shade_maw_bite")));
    public static final Supplier<SoundEvent> SHADE_STEP = SOUNDS.register("shade_step", () -> SoundEvent.createVariableRangeEvent(Companions.of("shade_step")));
    public static final Supplier<SoundEvent> SHADE_SWORD_SLASH = SOUNDS.register("shade_sword_slash", () -> SoundEvent.createVariableRangeEvent(Companions.of("shade_sword_slash")));
    public static final Supplier<SoundEvent> SHADE_SWORD_SPIN_SLASH = SOUNDS.register("shade_sword_spin_slash", () -> SoundEvent.createVariableRangeEvent(Companions.of("shade_sword_spin_slash")));

}