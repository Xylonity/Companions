package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class CompanionsSounds {

    public static void init() { ;; }

    public static final Supplier<SoundEvent> ANGEL_OF_GERTRUDE = registerSound("angel_of_gertrude", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "angel_of_gertrude")));
    public static final Supplier<SoundEvent> FLIP_CARD = registerSound("flip_card", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "flip_card")));
    public static final Supplier<SoundEvent> BONANZA = registerSound("bonanza", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "bonanza")));
    public static final Supplier<SoundEvent> COIN_CLATTER = registerSound("coin_clatter", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "coin_clatter")));
    public static final Supplier<SoundEvent> WRENCH_CONNECTION = registerSound("wrench_connection", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "wrench_connection")));
    public static final Supplier<SoundEvent> POP = registerSound("pop", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "pop")));
    public static final Supplier<SoundEvent> STAR_EXPLOSION = registerSound("star_explosion", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "star_explosion")));
    public static final Supplier<SoundEvent> SCROLL_SOUND = registerSound("scroll_sound", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "scroll_sound")));
    public static final Supplier<SoundEvent> SHADE_ALTAR_CHARGE = registerSound("shade_altar_charge", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "shade_altar_charge")));
    public static final Supplier<SoundEvent> SHADE_ALTAR_FULL = registerSound("shade_altar_full", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "shade_altar_full")));

    public static final Supplier<SoundEvent> SPELL_HIT_MARK = registerSound("spell_hit_mark", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "spell_hit_mark")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_BEAM = registerSound("spell_release_beam", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "spell_release_beam")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_BRACE = registerSound("spell_release_brace", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "spell_release_brace")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_DARK_HOLE = registerSound("spell_release_dark_hole", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "spell_release_dark_hole")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_HEAL = registerSound("spell_release_heal", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "spell_release_heal")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_ICE = registerSound("spell_release_ice", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "spell_release_ice")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_MARK = registerSound("spell_release_mark", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "spell_release_mark")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_SPEARS = registerSound("spell_release_spears", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "spell_release_spears")));
    public static final Supplier<SoundEvent> SPELL_RELEASE_TORNADO = registerSound("spell_release_tornado", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "spell_release_tornado")));

    public static final Supplier<SoundEvent> DINAMO_STEP = registerSound("dinamo_step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "dinamo_step")));
    public static final Supplier<SoundEvent> DINAMO_IDLE = registerSound("dinamo_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "dinamo_idle")));
    public static final Supplier<SoundEvent> DINAMO_HURT = registerSound("dinamo_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "dinamo_hurt")));
    public static final Supplier<SoundEvent> DINAMO_DEATH = registerSound("dinamo_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "dinamo_death")));
    public static final Supplier<SoundEvent> DINAMO_ATTACK = registerSound("dinamo_attack", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "dinamo_attack")));

    public static final Supplier<SoundEvent> MUTANT_TEDDY_ATTACK = registerSound("mutant_teddy_attack", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mutant_teddy_attack")));
    public static final Supplier<SoundEvent> MUTANT_TEDDY_FLAP_WINGS = registerSound("mutant_teddy_flap_wings", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mutant_teddy_flap_wings")));
    public static final Supplier<SoundEvent> MUTANT_TEDDY_HURT = registerSound("mutant_teddy_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mutant_teddy_hurt")));
    public static final Supplier<SoundEvent> MUTANT_TEDDY_IDLE = registerSound("mutant_teddy_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mutant_teddy_idle")));
    public static final Supplier<SoundEvent> TEDDY_ATTACK = registerSound("teddy_attack", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "teddy_attack")));
    public static final Supplier<SoundEvent> TEDDY_STEP = registerSound("teddy_step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "teddy_step")));
    public static final Supplier<SoundEvent> TEDDY_AUTO_STAB = registerSound("teddy_auto_stab", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "teddy_auto_stab")));
    public static final Supplier<SoundEvent> TEDDY_TRANSFORMATION = registerSound("teddy_transformation", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "teddy_transformation")));

    public static final Supplier<SoundEvent> ANTLION_DEATH = registerSound("antlion_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "antlion_death")));
    public static final Supplier<SoundEvent> ANTLION_HURT = registerSound("antlion_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "antlion_hurt")));
    public static final Supplier<SoundEvent> ANTLION_IDLE = registerSound("antlion_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "antlion_idle")));
    public static final Supplier<SoundEvent> ANTLION_STEPS = registerSound("antlion_steps", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "antlion_steps")));
    public static final Supplier<SoundEvent> ADULT_ANTLION_DEATH = registerSound("dragonfly_antlion_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "dragonfly_antlion_death")));
    public static final Supplier<SoundEvent> ADULT_ANTLION_FLY = registerSound("dragonfly_antlion_fly", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "dragonfly_antlion_fly")));
    public static final Supplier<SoundEvent> ADULT_ANTLION_HURT = registerSound("dragonfly_antlion_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "dragonfly_antlion_hurt")));
    public static final Supplier<SoundEvent> PUPA_ANTLION_STEP = registerSound("pupa_antlion_step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "pupa_antlion_step")));
    public static final Supplier<SoundEvent> SOLDIER_ANTLION_DEATH = registerSound("tank_antlion_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "tank_antlion_death")));
    public static final Supplier<SoundEvent> SOLDIER_ANTLION_HURT = registerSound("tank_antlion_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "tank_antlion_hurt")));
    public static final Supplier<SoundEvent> SOLDIER_ANTLION_IDLE = registerSound("tank_antlion_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "tank_antlion_idle")));
    public static final Supplier<SoundEvent> SOLDIER_ANTLION_SAND_CANNON = registerSound("tank_antlion_sand_cannon", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "tank_antlion_sand_cannon")));
    public static final Supplier<SoundEvent> SOLDIER_ANTLION_STEP = registerSound("tank_antlion_sand_step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "tank_antlion_sand_step")));

    public static final Supplier<SoundEvent> CLOAK_DEATH = registerSound("cloak_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "cloak_death")));
    public static final Supplier<SoundEvent> CLOAK_HURT = registerSound("cloak_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "cloak_hurt")));
    public static final Supplier<SoundEvent> CLOAK_IDLE = registerSound("cloak_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "cloak_idle")));
    public static final Supplier<SoundEvent> CLOAK_STEP = registerSound("cloak_step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "cloak_step")));
    public static final Supplier<SoundEvent> MANKH_BEAM = registerSound("mankh_beam", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mankh_beam")));
    public static final Supplier<SoundEvent> MANKH_DEATH = registerSound("mankh_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mankh_death")));
    public static final Supplier<SoundEvent> MANKH_RING = registerSound("mankh_ring", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mankh_ring")));
    public static final Supplier<SoundEvent> MANKH_STEP = registerSound("mankh_step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mankh_step")));

    public static final Supplier<SoundEvent> HOLINESS_DEATH = registerSound("holiness_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "holiness_death")));
    public static final Supplier<SoundEvent> HOLINESS_FLY_OFF = registerSound("holiness_fly_off", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "holiness_fly_off")));
    public static final Supplier<SoundEvent> HOLINESS_HIT_CHEST = registerSound("holiness_hit_chest", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "holiness_hit_chest")));
    public static final Supplier<SoundEvent> HOLINESS_HIT_GROUND = registerSound("holiness_hit_ground", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "holiness_hit_ground")));
    public static final Supplier<SoundEvent> HOLINESS_STAB = registerSound("holiness_stab", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "holiness_stab")));
    public static final Supplier<SoundEvent> HOLINESS_STAKE = registerSound("holiness_stake", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "holiness_stake")));
    public static final Supplier<SoundEvent> HOLINESS_STAR_SPAWN = registerSound("holiness_star_spawn", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "holiness_star_spawn")));
    public static final Supplier<SoundEvent> PONTIFF_ACTIVATE = registerSound("pontiff_activate", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "pontiff_activate")));
    public static final Supplier<SoundEvent> PONTIFF_AREA_ATTACK = registerSound("pontiff_area_attack", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "pontiff_area_attack")));
    public static final Supplier<SoundEvent> PONTIFF_FRONT_ATTACK = registerSound("pontiff_front_attack", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "pontiff_front_attack")));
    public static final Supplier<SoundEvent> PONTIFF_GROUND_DESPAWN = registerSound("pontiff_ground_despawn", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "pontiff_ground_despawn")));
    public static final Supplier<SoundEvent> PONTIFF_HURT = registerSound("pontiff_ground_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "pontiff_ground_hurt")));
    public static final Supplier<SoundEvent> PONTIFF_IDLE = registerSound("pontiff_ground_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "pontiff_ground_idle")));
    public static final Supplier<SoundEvent> PONTIFF_STEP = registerSound("pontiff_step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "pontiff_step")));

    public static final Supplier<SoundEvent> CROISSANT_DRAGON_BREATH = registerSound("croissant_dragon_breath", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "croissant_dragon_breath")));
    public static final Supplier<SoundEvent> CROISSANT_DRAGON_CREAM_SPLAT = registerSound("croissant_dragon_cream_splat", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "croissant_dragon_cream_splat")));
    public static final Supplier<SoundEvent> CROISSANT_DRAGON_CREAM_DEATH = registerSound("croissant_dragon_cream_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "croissant_dragon_cream_death")));
    public static final Supplier<SoundEvent> CROISSANT_DRAGON_CREAM_HURT = registerSound("croissant_dragon_cream_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "croissant_dragon_cream_hurt")));
    public static final Supplier<SoundEvent> CROISSANT_DRAGON_CREAM_IDLE = registerSound("croissant_dragon_cream_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "croissant_dragon_cream_idle")));
    public static final Supplier<SoundEvent> CROISSANT_DRAGON_CREAM_STEPS = registerSound("croissant_dragon_cream_steps", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "croissant_dragon_cream_steps")));
    public static final Supplier<SoundEvent> CROISSANT_DRAGON_CREAM_WING_FLAP = registerSound("croissant_dragon_cream_wing_flap", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "croissant_dragon_cream_wing_flap")));

    public static final Supplier<SoundEvent> END_FROG_HEAL = registerSound("end_frog_heal", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "end_frog_heal")));
    public static final Supplier<SoundEvent> FROGGY_ATTACK = registerSound("froggy_attack", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "froggy_attack")));
    public static final Supplier<SoundEvent> FROGGY_DEATH = registerSound("froggy_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "froggy_death")));
    public static final Supplier<SoundEvent> FROGGY_HURT = registerSound("froggy_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "froggy_hurt")));
    public static final Supplier<SoundEvent> FROGGY_IDLE = registerSound("froggy_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "froggy_idle")));
    public static final Supplier<SoundEvent> FROGGY_JUMP = registerSound("froggy_jump", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "froggy_jump")));
    public static final Supplier<SoundEvent> MID_FROG_DEATH = registerSound("mid_frog_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mid_frog_death")));
    public static final Supplier<SoundEvent> MID_FROG_IDLE = registerSound("mid_frog_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mid_frog_idle")));
    public static final Supplier<SoundEvent> MID_FROG_JUMP = registerSound("mid_frog_jump", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mid_frog_jump")));
    public static final Supplier<SoundEvent> NETHER_BULLFROG_AIR_SLASH_END = registerSound("nether_bullfrog_air_slash_end", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "nether_bullfrog_air_slash_end")));
    public static final Supplier<SoundEvent> NETHER_BULLFROG_DEATH = registerSound("nether_bullfrog_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "nether_bullfrog_death")));
    public static final Supplier<SoundEvent> NETHER_BULLFROG_HURT = registerSound("nether_bullfrog_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "nether_bullfrog_hurt")));
    public static final Supplier<SoundEvent> NETHER_BULLFROG_IDLE = registerSound("nether_bullfrog_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "nether_bullfrog_idle")));
    public static final Supplier<SoundEvent> NETHER_BULLFROG_JUMP = registerSound("nether_bullfrog_jump", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "nether_bullfrog_jump")));
    public static final Supplier<SoundEvent> NETHER_BULLFROG_SLASH = registerSound("nether_bullfrog_slash", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "nether_bullfrog_slash")));
    public static final Supplier<SoundEvent> SMALL_FROG_DEATH = registerSound("small_frog_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "small_frog_death")));
    public static final Supplier<SoundEvent> SMALL_FROG_HURT = registerSound("small_frog_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "small_frog_hurt")));
    public static final Supplier<SoundEvent> SMALL_FROG_IDLE = registerSound("small_frog_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "small_frog_idle")));
    public static final Supplier<SoundEvent> SMALL_FROG_JUMP = registerSound("small_frog_jump", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "small_frog_jump")));
    public static final Supplier<SoundEvent> SMALL_FROG_SHOOT = registerSound("small_frog_shoot", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "small_frog_shoot")));

    public static final Supplier<SoundEvent> MAGE_DEATH = registerSound("mage_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mage_death")));
    public static final Supplier<SoundEvent> MAGE_GET_BOOK = registerSound("mage_get_book", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mage_get_book")));
    public static final Supplier<SoundEvent> MAGE_HURT = registerSound("mage_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mage_hurt")));
    public static final Supplier<SoundEvent> MAGE_IDLE = registerSound("mage_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mage_idle")));
    public static final Supplier<SoundEvent> MAGE_STEP = registerSound("mage_step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "mage_step")));

    public static final Supplier<SoundEvent> END_MINION_DEATH = registerSound("end_minion_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "end_minion_death")));
    public static final Supplier<SoundEvent> END_MINION_FLAP = registerSound("end_minion_flap", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "end_minion_flap")));
    public static final Supplier<SoundEvent> END_MINION_HURT = registerSound("end_minion_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "end_minion_hurt")));
    public static final Supplier<SoundEvent> END_MINION_STEP = registerSound("end_minion_step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "end_minion_step")));
    public static final Supplier<SoundEvent> NETHER_MINION_DEATH = registerSound("nether_minion_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "nether_minion_death")));
    public static final Supplier<SoundEvent> NETHER_MINION_HURT = registerSound("nether_minion_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "nether_minion_hurt")));
    public static final Supplier<SoundEvent> NETHER_MINION_IDLE = registerSound("nether_minion_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "nether_minion_idle")));
    public static final Supplier<SoundEvent> OVERWORLD_MINION_DEATH = registerSound("overworld_minion_death", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "overworld_minion_death")));
    public static final Supplier<SoundEvent> OVERWORLD_MINION_HURT = registerSound("overworld_minion_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "overworld_minion_hurt")));
    public static final Supplier<SoundEvent> OVERWORLD_MINION_IDLE = registerSound("overworld_minion_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "overworld_minion_idle")));

    public static final Supplier<SoundEvent> HOSTILE_PUPPET_GLOVE_ATTACK = registerSound("hostile_puppet_glove_attack", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "hostile_puppet_glove_attack")));
    public static final Supplier<SoundEvent> HOSTILE_PUPPET_GLOVE_CLEAN = registerSound("hostile_puppet_glove_clean", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "hostile_puppet_glove_clean")));
    public static final Supplier<SoundEvent> HOSTILE_PUPPET_GLOVE_LOOSE = registerSound("hostile_puppet_glove_loose", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "hostile_puppet_glove_loose")));
    public static final Supplier<SoundEvent> PUPPET_ATTACK_BLADE = registerSound("puppet_attack_blade", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "puppet_attack_blade")));
    public static final Supplier<SoundEvent> PUPPET_ATTACK_CANON = registerSound("puppet_attack_canon", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "puppet_attack_canon")));
    public static final Supplier<SoundEvent> PUPPET_ATTACK_MUTANT = registerSound("puppet_attack_mutant", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "puppet_attack_mutant")));
    public static final Supplier<SoundEvent> PUPPET_ATTACK_WHIP = registerSound("puppet_attack_whip", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "puppet_attack_whip")));
    public static final Supplier<SoundEvent> PUPPET_EQUIP_BLADE = registerSound("puppet_equip_blade", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "puppet_equip_blade")));
    public static final Supplier<SoundEvent> PUPPET_EQUIP_CANON = registerSound("puppet_equip_canon", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "puppet_equip_canon")));
    public static final Supplier<SoundEvent> PUPPET_EQUIP_MUTANT = registerSound("puppet_equip_mutant", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "puppet_equip_mutant")));
    public static final Supplier<SoundEvent> PUPPET_EQUIP_WHIP = registerSound("puppet_equip_whip", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "puppet_equip_whip")));
    public static final Supplier<SoundEvent> PUPPET_GLOVE_SHOOT = registerSound("puppet_glove_shoot", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "puppet_glove_shoot")));
    public static final Supplier<SoundEvent> PUPPET_GLOVE_STEP = registerSound("puppet_glove_step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "puppet_glove_step")));
    public static final Supplier<SoundEvent> PUPPET_HURT = registerSound("puppet_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "puppet_hurt")));
    public static final Supplier<SoundEvent> PUPPET_WALK = registerSound("puppet_walk", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "puppet_walk")));

    public static final Supplier<SoundEvent> SHADE_BELL_SUMMON = registerSound("shade_bell_summon", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "shade_bell_summon")));
    public static final Supplier<SoundEvent> SHADE_DESPAWN = registerSound("shade_despawn", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "shade_despawn")));
    public static final Supplier<SoundEvent> SHADE_HURT = registerSound("shade_hurt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "shade_hurt")));
    public static final Supplier<SoundEvent> SHADE_IDLE = registerSound("shade_idle", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "shade_idle")));
    public static final Supplier<SoundEvent> SHADE_MAW_BITE = registerSound("shade_maw_bite", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "shade_maw_bite")));
    public static final Supplier<SoundEvent> SHADE_STEP = registerSound("shade_step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "shade_step")));
    public static final Supplier<SoundEvent> SHADE_SWORD_SLASH = registerSound("shade_sword_slash", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "shade_sword_slash")));
    public static final Supplier<SoundEvent> SHADE_SWORD_SPIN_SLASH = registerSound("shade_sword_spin_slash", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CompanionsCommon.MOD_ID, "shade_sword_spin_slash")));

    private static <T extends SoundEvent> Supplier<T> registerSound(String id, Supplier<T> sound) {
        return CompanionsCommon.COMMON_PLATFORM.registerSound(id, sound);
    }

}