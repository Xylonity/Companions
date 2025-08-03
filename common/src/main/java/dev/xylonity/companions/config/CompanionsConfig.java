package dev.xylonity.companions.config;

import dev.xylonity.knightlib.config.api.AutoConfig;
import dev.xylonity.knightlib.config.api.ConfigEntry;

@AutoConfig(file = "companions")
public final class CompanionsConfig {

    @ConfigEntry(
            category = "General",
            comment = "Probability (0.0–1.0) that demon flesh will drop when defeating certain enemies.",
            min = 0.0, max = 1.0
    )
    public static double DEMON_FLESH_DROP_RATE = 0.2;

    @ConfigEntry(
            category = "General",
            comment = "Respawn totem spawn charges when activated.",
            min = 0, max = 200
    )
    public static int RESPAWN_TOTEM_CHARGES = 5;

    @ConfigEntry(
            category = "General",
            comment = "Maximum number of charges a single shade altar can store.",
            min = 0, max = 1000
    )
    public static int SHADOW_ALTAR_MAX_CHARGES = 20;

    @ConfigEntry(
            category = "General",
            comment = "Number of blood charges a shade altar can hold.",
            min = 0, max = 1000
    )
    public static int SHADOW_ALTAR_BLOOD_CHARGES_AMOUNT = 4;

    @ConfigEntry(
            category = "General",
            comment = "Whether companions are allowed to wander when neither following nor sitting."
    )
    public static boolean SHOULD_COMPANIONS_WANDER = true;

    @ConfigEntry(
            category = "General",
            comment = "The maximum distance (in blocks) at which companions will teleport to their owner (in a valid position).",
            min = 0, max = 1000
    )
    public static int COMPANIONS_FOLLOW_OWNER_TELEPORT_DISTANCE = 20;

    /**
     * DINAMO
     */

    @ConfigEntry(
            category = "Dinamo",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks, "
                    + "and it will not visually render the Tesla connection rays. "
                    + "Keeping the chunk loaded is therefore necessary for full functionality in this entity."
    )
    public static boolean DINAMO_KEEP_CHUNK_LOADED = true;

    @ConfigEntry(
            category = "Dinamo",
            comment = "The maximum number of tesla modules that can be connected in a chain without using a voltaic repeater, starting from a Dinamo. "
                    + "For example, if set to 3, the Dinamo can connect to coil 1, which connects to coil 2, which connects to coil 3.",
            note = "Any additional tesla modules beyond this limit will not be powered.",
            min = 0, max = 50
    )
    public static int DINAMO_MAX_CHAIN_CONNECTIONS = 7;

    @ConfigEntry(
            category = "Dinamo",
            comment = "The maximum distance a tesla module can connect to another",
            min = 0, max = 100
    )
    public static int DINAMO_MAX_CONNECTION_DISTANCE = 13;

    @ConfigEntry(
            category = "Dinamo",
            comment = "Max life of the Dinamo",
            min = 0d, max = 5000d
    )
    public static double DINAMO_MAX_LIFE = 65d;

    @ConfigEntry(
            category = "Dinamo",
            comment = "Electricity damage from both Dinamo and tesla network blocks.",
            min = 0d, max = 500d
    )
    public static double ELECTRICITY_DAMAGE = 7.5d;

    /**
     * ANTLION
     */

    @ConfigEntry(
            category = "Antlion",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean ANTLION_KEEP_CHUNK_LOADED = true;

    @ConfigEntry(
            category = "Antlion",
            comment = "Damage dealt by the Antlion in its normal phase",
            min = 0.0, max = 500.0
    )
    public static double ANTLION_NORMAL_DAMAGE = 7.0;

    @ConfigEntry(
            category = "Antlion",
            comment = "Max life of the Antlion in its normal phase",
            min = 0, max = 1000
    )
    public static double ANTLION_NORMAL_MAX_LIFE = 70;

    @ConfigEntry(
            category = "Antlion",
            comment = "Damage dealt by the Antlion in its soldier phase",
            min = 0.0, max = 1000.0
    )
    public static double ANTLION_SOLDIER_DAMAGE = 10.0;

    @ConfigEntry(
            category = "Antlion",
            comment = "Max life of the Antlion in its soldier phase",
            min = 0, max = 2000
    )
    public static double ANTLION_SOLDIER_MAX_LIFE = 70;

    @ConfigEntry(
            category = "Antlion",
            comment = "Damage dealt by the Antlion in its flying phase",
            min = 0.0, max = 100.0
    )
    public static double ANTLION_FLYER_DAMAGE = 10.0;

    @ConfigEntry(
            category = "Antlion",
            comment = "Max life of the Antlion in its flying phase",
            min = 0, max = 1500
    )
    public static double ANTLION_FLYER_MAX_LIFE = 45;

    @ConfigEntry(
            category = "Antlion",
            comment = "Damage dealt by the Antlion in its tank phase",
            min = 0.0, max = 200.0
    )
    public static double ANTLION_TANK_DAMAGE = 6.0;

    @ConfigEntry(
            category = "Antlion",
            comment = "Max life of the Antlion in its tank phase",
            min = 0, max = 5000
    )
    public static double ANTLION_TANK_MAX_LIFE = 120.0;

    @ConfigEntry(
            category = "Antlion",
            comment = "Damage dealt by the Antlion's sand projectile in its soldier phase",
            min = 0.0, max = 200.0
    )
    public static double ANTLION_SOLDIER_PROJECTILE_DAMAGE = 10f;

    /**
     * CORNELIUS
     */

    @ConfigEntry(
            category = "Cornelius",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean CORNELIUS_KEEP_CHUNK_LOADED = true;

    @ConfigEntry(
            category = "Cornelius",
            comment = "Ender Frog Heal Projectile heal amount",
            min = 0d, max = 100d
    )
    public static double ENDER_FROG_HEAL_PROJECTILE_HEAL_AMOUNT = 2.5d;

    @ConfigEntry(
            category = "Cornelius",
            comment = "Max life of the Cornelius",
            min = 0d, max = 5000d
    )
    public static double CORNELIUS_MAX_LIFE = 70d;

    /**
     * CROISSANT DRAGON
     */

    @ConfigEntry(
            category = "Croissant Dragon",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean CROISSANT_DRAGON_KEEP_CHUNK_LOADED = true;

    @ConfigEntry(
            category = "Croissant Dragon",
            comment = "The amount of time the croissant dragon needs to hatch.",
            note = "The time is measured in ticks",
            min = 10, max = 100000
    )
    public static int CROISSANT_EGG_LIFETIME = 6000;

    @ConfigEntry(
            category = "Croissant Dragon",
            comment = "Max life of the Croissant Dragon",
            min = 0d, max = 5000d
    )
    public static double CROISSANT_DRAGON_MAX_LIFE = 80d;

    /**
     * TEDDY
     */

    @ConfigEntry(
            category = "Teddy",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean TEDDY_KEEP_CHUNK_LOADED = true;

    @ConfigEntry(
            category = "Teddy",
            comment = "Max life of the Teddy",
            min = 0d, max = 5000d
    )
    public static double TEDDY_MAX_LIFE = 70d;

    @ConfigEntry(
            category = "Teddy",
            comment = "Damage dealt by the Teddy",
            min = 0.0, max = 50.0
    )
    public static double TEDDY_DAMAGE = 7d;

    @ConfigEntry(
            category = "Teddy",
            comment = "Max life of the Teddy in mutant phase",
            min = 0, max = 2000
    )
    public static double TEDDY_MUTANT_MAX_LIFE = 130;

    @ConfigEntry(
            category = "Teddy",
            comment = "Damage dealt by the Teddy in mutant phase",
            min = 0.0, max = 100.0
    )
    public static double TEDDY_MUTANT_DAMAGE = 8.5d;

    @ConfigEntry(
            category = "Teddy",
            comment = "Should the Teddy heal over time in its mutant form?"
    )
    public static boolean TEDDY_MUTANT_HEALS_OVER_TIME = true;


    /**
     * SOUL MAGE
     */

    @ConfigEntry(
            category = "Soul Mage",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean SOUL_MAGE_KEEP_CHUNK_LOADED = true;

    @ConfigEntry(
            category = "Soul Mage",
            comment = "Max life of the Soul Mage",
            min = 0d, max = 5000d
    )
    public static double SOUL_MAGE_MAX_LIFE = 75d;

    /**
     * SHADE SWORD
     */

    @ConfigEntry(
            category = "Shadow Sword",
            comment = "The amount of lifetime of the shadow sword",
            note = "The time is measured in ticks (20 ticks = 1 second)",
            min = 0, max = 100000
    )
    public static int SHADOW_SWORD_LIFETIME = 4800;

    @ConfigEntry(
            category = "Shadow Sword",
            comment = "Max life of the Shadow Sword",
            min = 0d, max = 5000d
    )
    public static double SHADOW_SWORD_MAX_LIFE = 70d;

    @ConfigEntry(
            category = "Shadow Sword",
            comment = "Damage dealt by the Shadow Sword",
            min = 0.0, max = 500.0
    )
    public static double SHADOW_SWORD_DAMAGE = 8.0;

    @ConfigEntry(
            category = "Shadow Sword",
            comment = "Shadow Sword stats multiplier on its blood state. For example, if set to 1.2, a default damage of 10 will be 12 when the blood upgrade is active. This is only applied to Health and Attack Damage attributes.",
            min = 0.0, max = 10.0
    )
    public static double SHADOW_SWORD_BLOOD_MULTIPLIER = 1.2;

    /**
     * SHADE MAW
     */

    @ConfigEntry(
            category = "Shadow Maw",
            comment = "The amount of lifetime of the shadow maw",
            note = "The time is measured in ticks (20 ticks = 1 second)",
            min = 0, max = 100000
    )
    public static int SHADOW_MAW_LIFETIME = 4800;

    @ConfigEntry(
            category = "Shadow Maw",
            comment = "Max life of the Shadow Maw",
            min = 0d, max = 5000d
    )
    public static double SHADOW_MAW_MAX_LIFE = 100d;

    @ConfigEntry(
            category = "Shadow Maw",
            comment = "Damage dealt by the Shadow Maw",
            min = 0.0, max = 500.0
    )
    public static double SHADOW_MAW_DAMAGE = 6.0;

    @ConfigEntry(
            category = "Shadow Maw",
            comment = "Shadow Maw stats multiplier on its blood state. For example, if set to 1.2, a default damage of 10 will be 12 when the blood upgrade is active. This is only applied to Health and Attack Damage attributes.",
            min = 0.0, max = 10.0
    )
    public static double SHADOW_MAW_BLOOD_MULTIPLIER = 1.2;

    /**
     * MINION
     */

    @ConfigEntry(
            category = "Minion",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean MINION_KEEP_CHUNK_LOADED = true;

    @ConfigEntry(
            category = "Minion",
            comment = "Max life of the Minion",
            min = 0d, max = 5000d
    )
    public static double MINION_MAX_LIFE = 70d;

    /**
     * MANKH
     */

    @ConfigEntry(
            category = "Mankh",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean MANKH_KEEP_CHUNK_LOADED = true;

    @ConfigEntry(
            category = "Mankh",
            comment = "Max life of the Mankh",
            min = 0, max = 5000
    )
    public static double MANKH_MAX_LIFE = 85;

    @ConfigEntry(
            category = "Mankh",
            comment = "Damage dealt by the Mankh",
            min = 0.0, max = 500.0
    )
    public static double MANKH_DAMAGE = 8.5;


    /**
     * CLOAK
     */

    @ConfigEntry(
            category = "Cloak",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean CLOAK_KEEP_CHUNK_LOADED = true;

    @ConfigEntry(
            category = "Cloak",
            comment = "Max life of the Cloak",
            min = 0d, max = 5000d
    )
    public static double CLOAK_MAX_LIFE = 75d;

    @ConfigEntry(
            category = "Cloak",
            comment = "Damage dealt by the Cloak",
            min = 0.0, max = 500.0
    )
    public static double CLOAK_DAMAGE = 8.0;

    /**
     * PUPPET
     */

    @ConfigEntry(
            category = "Puppet",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean PUPPET_KEEP_CHUNK_LOADED = true;

    @ConfigEntry(
            category = "Puppet",
            comment = "Max life of the Puppet",
            min = 0, max = 1000
    )
    public static double PUPPET_MAX_LIFE = 85;

    @ConfigEntry(
            category = "Puppet",
            comment = "Damage dealt by the Puppet",
            min = 0.0, max = 100.0
    )
    public static double PUPPET_DAMAGE = 7.0;

    /**
     * PUPPET GLOVE
     */

    @ConfigEntry(
            category = "Puppet Glove",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean PUPPET_GLOVE_KEEP_CHUNK_LOADED = true;

    @ConfigEntry(
            category = "Puppet Glove",
            comment = "Max life of the Puppet Glove",
            min = 0d, max = 1000d
    )
    public static double PUPPET_GLOVE_MAX_LIFE = 35;

    @ConfigEntry(
            category = "Puppet Glove",
            comment = "Damage dealt by the Puppet Glove",
            min = 0.0, max = 100.0
    )
    public static double PUPPET_GLOVE_DAMAGE = 6.5;

    /**
     * SACRED PONTIFF
     */

    @ConfigEntry(
            category = "Sacred Pontiff",
            comment = "Max life of the Sacred Pontiff",
            min = 0d, max = 5000d
    )
    public static double SACRED_PONTIFF_MAX_LIFE = 225d;

    @ConfigEntry(
            category = "Sacred Pontiff",
            comment = "Damage dealt by the Sacred Pontiff",
            min = 0.0, max = 500.0
    )
    public static double SACRED_PONTIFF_DAMAGE = 7.0;

    @ConfigEntry(
            category = "Sacred Pontiff",
            comment = "Max life of His Holiness",
            min = 0d, max = 5000d
    )
    public static double HIS_HOLINESS_MAX_LIFE = 350d;

    @ConfigEntry(
            category = "Sacred Pontiff",
            comment = "Damage dealt by His Holiness",
            min = 0.0, max = 500.0
    )
    public static double HIS_HOLINESS_DAMAGE = 9.0;

    /**
     * MAGIC BOOKS
     */

    @ConfigEntry(
            category = "Magic Books",
            comment = "Damage dealt by each fragment of the magic ray.",
            min = 1.0D, max = 1000.0D
    )
    public static double MAGIC_PIECE_DAMAGE = 5.5d;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Damage dealt by the ice tornado spell.",
            min = 1.0D, max = 1000.0D
    )
    public static double ICE_TORNADO_DAMAGE = 3.0d;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Damage dealt by the stone spike spell.",
            min = 1.0D, max = 1000.0D
    )
    public static double STONE_SPIKE_DAMAGE = 4.0d;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Radius (in blocks) of the Fire Mark spell's effect.",
            min = 1.0D, max = 1000.0D
    )
    public static double FIRE_MARK_EFFECT_RADIUS = 2.5D;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Amount of health restored by the healing ring spell.",
            min = 0.0D, max = 1000.0D
    )
    public static double HEAL_RING_HEALING = 6.0D;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Attraction radius (in blocks) of the black hole spell.",
            min = 0.0D, max = 1000.0D
    )
    public static double BLACK_HOLE_ATTRACTION_RADIUS = 12.0D;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Speed at which entities are pulled toward the black hole.",
            min = 0.0D, max = 100.0D
    )
    public static double BLACK_HOLE_ATTRACTION_SPEED = 1.45d;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Damage dealt by each small ice shard.",
            min = 0.0D, max = 100.0D
    )
    public static double SMALL_ICE_SHARD_DAMAGE = 2.5d;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Duration (in ticks) that a single small ice shard freezes a target",
            min = 0, max = 10000
    )
    public static int SMALL_ICE_SHARD_FREEZE_TICKS = 100;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Cooldown (in ticks) of the black hole magic book",
            min = 0, max = 10000
    )
    public static int BLACK_HOLE_COOLDOWN = 160;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Cooldown (in ticks) of the naginata magic book",
            min = 0, max = 10000
    )
    public static int NAGINATA_COOLDOWN = 140;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Cooldown (in ticks) of the brace magic book",
            min = 0, max = 10000
    )
    public static int BRACE_COOLDOWN = 160;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Cooldown (in ticks) of the fire mark magic book",
            min = 0, max = 10000
    )
    public static int FIRE_MARK_COOLDOWN = 140;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Cooldown (in ticks) of the heal ring magic book",
            min = 0, max = 10000
    )
    public static int HEAL_RING_COOLDOWN = 200;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Cooldown (in ticks) of the ice shard magic book",
            min = 0, max = 10000
    )
    public static int ICE_SHARD_COOLDOWN = 180;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Cooldown (in ticks) of the ice tornado magic book",
            min = 0, max = 10000
    )
    public static int ICE_TORNADO_COOLDOWN = 200;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Cooldown (in ticks) of the magic ray magic book",
            min = 0, max = 10000
    )
    public static int MAGIC_RAY_COOLDOWN = 200;

    @ConfigEntry(
            category = "Magic Books",
            comment = "Cooldown (in ticks) of the stone spikes magic book",
            min = 0, max = 10000
    )
    public static int STONE_SPIKES_COOLDOWN = 140;

    /**
     * ARMOURY
     */

    @ConfigEntry(
            category = "Armoury",
            comment = "Crystallized Blood armor stats: [helmet, chest, legs, boots, toughness, knockbackRes, durabilityMult]. Don't use negative values and stick to the format."
    )
    public static String CRYSTALLIZED_BLOOD_SET_STATS = "3, 8, 6, 3, 2.5, 0.05, 35";

    @ConfigEntry(
            category = "Armoury",
            comment = "Mage armor stats: [helmet, chest, legs, boots, toughness, knockbackRes, durabilityMult]. Don't use negative values and stick to the format."
    )
    public static String MAGE_SET_STATS = "3, 8, 6, 3, 2.5, 0.05, 35";

    @ConfigEntry(
            category = "Armoury",
            comment = "Holy Robe armor stats: [helmet, chest, legs, boots, toughness, knockbackRes, durabilityMult]. Don't use negative values and stick to the format."
    )
    public static String HOLY_ROBE_SET_STATS = "3, 8, 6, 3, 2.5, 0.05, 35";

    @ConfigEntry(
            category = "Armoury",
            comment = "Netherite Dagger stats: [miningLvl, durability, miningSpeed, baseDmg, enchantability]. Don't use negative values and stick to the format."
    )
    public static String NETHERITE_DAGGER_STATS = "4, 1300, 6, 1, 15";

    @ConfigEntry(
            category = "Armoury",
            comment = "Crystallized Blood weapon stats: [miningLvl, durability, miningSpeed, baseDmg, enchant, axeDmg, scytheDmg, swordDmg, axeSpd, scytheSpd, swordSpd]. Stick to the format."
    )
    public static String CRYSTALLIZED_BLOOD_WEAPON_STATS = "4, 2031, 9, 4, 15, 5, 1, 3, -3, -2.8, -2.4";

    @ConfigEntry(
            category = "Armoury",
            comment = "Damage reduction per hit per piece of Crystallized Blood armor.",
            min = 0d, max = 1d
    )
    public static double CRYSTALLIZED_BLOOD_SET_REDUCTION = 0.1d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Crystallized Blood Axe cooldown",
            min = 0, max = 10000
    )
    public static int CRYSTALLIZED_BLOOD_AXE_COOLDOWN = 200;

    @ConfigEntry(
            category = "Armoury",
            comment = "Crystallized Blood Sword cooldown",
            min = 0, max = 10000
    )
    public static int CRYSTALLIZED_BLOOD_SWORD_COOLDOWN = 200;

    @ConfigEntry(
            category = "Armoury",
            comment = "Minimum health % required to activate Crystallized Blood armor effect.",
            min = 0d, max = 1d
    )
    public static double CRYSTALLIZED_BLOOD_SET_MIN_HEALTH = 0.5d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Magic damage reduction per hit per piece of Mage armor.",
            min = 0d, max = 1d
    )
    public static double MAGE_SET_DAMAGE_REDUCTION = 0.1d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Cooldown reduction fraction for Mage armor when using magic books. Reduction per piece.",
            min = 0d, max = 1d
    )
    public static double MAGE_SET_COOLDOWN_REDUCTION = 0.15d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Damage reduction per piece of Holy Robe armor.",
            min = 0d, max = 1d
    )
    public static double HOLY_ROBE_DAMAGE_REDUCTION = 0.07d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Chance per hit to spawn a Fire Ring from Holy Robe armor.",
            min = 0d, max = 1d
    )
    public static double HOLY_ROBE_FIRE_RING_SPAWN_CHANCE = 0.1d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Damage dealt by Naginata weapon.",
            min = 0d, max = 1000d
    )
    public static double NAGINATA_DAMAGE = 8d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Damage dealt by Fire Ring ability.",
            min = 0d, max = 1000d
    )
    public static double FIRE_RING_DAMAGE = 5d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Damage dealt by Needle projectile.",
            min = 0d, max = 1000d
    )
    public static double NEEDLE_PROJECTILE_DAMAGE = 5d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Damage dealt by Needle item on hit (both player and target).",
            min = 0d, max = 1000d
    )
    public static double NEEDLE_ITEM_DAMAGE = 2d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Damage dealt by Holiness Star when thrown.",
            min = 0d, max = 1000d
    )
    public static double HOLINESS_STAR_DAMAGE = 8d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Damage dealt by Brace projectile per hit.",
            min = 0d, max = 1000d
    )
    public static double BRACE_PROJECTILE_DAMAGE = 4d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Lifesteal fraction per hit for Crystallized Blood Scythe.",
            note = "0.10 = heal 10% of damage dealt",
            min = 0d, max = 1d
    )
    public static double CRYSTALLIZED_BLOOD_SCYTHE_LIFE_STEAL = 0.15d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Damage dealt by Blood Tornado (axe ability).",
            min = 0d, max = 1000d
    )
    public static double BLOOD_TORNADO_DAMAGE = 4d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Damage dealt by Blood Slash (sword ability).",
            min = 0d, max = 1000d
    )
    public static double BLOOD_SLASH_DAMAGE = 7.5d;

    @ConfigEntry(
            category = "Armoury",
            comment = "Travel speed of Blood Slash projectile.",
            min = 0d, max = 1000d
    )
    public static double BLOOD_SLASH_SPEED = 0.5225d;

    @ConfigEntry(
            category = "Spawn Rates",
            comment = "Golden allay spawnrate: [weight, minAmount, maxAmount, biomes and tags... (as many as you want)]"
    )
    public static String GOLDEN_ALLAY_SPAWN = "20, 1, 1, minecraft:swamp, minecraft:mangrove_swamp, #forge:is_swamp";

    @ConfigEntry(
            category = "Spawn Rates",
            comment = "Cornelius spawnrate: [weight, minAmount, maxAmount, biomes and tags... (as many as you want)]"
    )
    public static String CORNELIUS_SPAWN = "15, 1, 1, minecraft:swamp, minecraft:mangrove_swamp, #forge:is_swamp";

    @ConfigEntry(
            category = "Spawn Rates",
            comment = "Wild Antlion spawnrate: [weight, minAmount, maxAmount, biomes and tags... (as many as you want)]"
    )
    public static String WILD_ANTLION_SPAWN = "15, 1, 1, minecraft:desert, #forge:is_desert";

    @ConfigEntry(
            category = "Food",
            comment = "Should the mutant flesh tp the player to the opposite position when looking at a target?"
    )
    public static boolean MUTANT_FLESH_SHOULD_TP = true;

    @ConfigEntry(
            category = "Food",
            comment = "Should the mutant flesh hurt the player?"
    )
    public static boolean MUTANT_FLESH_SHOULD_HURT = true;

    @ConfigEntry(
            category = "Food",
            comment = "Should the antlion fur set the player on fire?"
    )
    public static boolean ANTLION_FUR_SHOULD_FIRE = true;

    @ConfigEntry(
            category = "Bonanza",
            comment = "Bonanza item reward drops from 2 teddy heads: [item, chance, amount (can be either exact, n, or ranged, n-m); ...]"
    )
    public static String BONANZA_2_TEDDY_HEADS_DROPS =
            "minecraft:golden_apple, 1.0, 1;" +
            "minecraft:feather, 1.0, 1-4;" +
            "minecraft:ender_pearl, 0.5, 1;" +
            "minecraft:enchanted_golden_apple, 0.25, 1;" +
            "minecraft:netherite_scrap, 0.05, 1;" +
            "minecraft:wither_skeleton_skull, 0.10, 1;" +
            "minecraft:axolotl_bucket, 0.10, 1;" +
            "minecraft:music_disc_pigstep, 0.05, 1;" +
            "minecraft:diamond, 0.15, 1;" +
            "minecraft:emerald, 0.20, 1-2;" +
            "minecraft:experience_bottle, 0.10, 1-3;" +
            "minecraft:nether_star, 0.005, 1";

    @ConfigEntry(
            category = "Bonanza",
            comment = "Bonanza item reward drops from 3 teddy heads: [item, chance, amount (can be either exact, n, or ranged, n-m); ...]"
    )
    public static String BONANZA_3_TEDDY_HEADS_DROPS =
            "minecraft:golden_apple, 1.0, 1-3;" +
            "minecraft:enchanted_golden_apple, 0.8, 1-3;" +
            "minecraft:ender_pearl, 0.75, 1-2;" +
            "minecraft:ender_eye, 0.75, 1-2;" +
            "minecraft:diamond, 0.65, 1-9;" +
            "minecraft:gold_ingot, 0.85, 1-19;" +
            "minecraft:cake, 0.55, 1;" +
            "minecraft:netherite_scrap, 0.30, 1;" +
            "minecraft:netherite_ingot, 0.10, 1;" +
            "minecraft:nether_star, 0.05, 1;" +
            "minecraft:enchanted_book, 0.15, 1;" +
            "minecraft:enchanted_book, 0.10, 1";

    @ConfigEntry(
            category = "Bonanza",
            comment = "Bonanza entity spawns from 3 skull heads: [entity, amount; ...]"
    )
    public static String BONANZA_3_SKULL_DROP_ENTITIES = "minecraft:warden, 1";

}