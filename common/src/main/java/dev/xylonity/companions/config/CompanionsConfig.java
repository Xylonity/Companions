package dev.xylonity.companions.config;

import dev.xylonity.knightlib.config.api.AutoConfig;
import dev.xylonity.knightlib.config.api.ConfigEntry;
import dev.xylonity.knightlib.config.api.DecorationType;

@AutoConfig(file = "companions", style = DecorationType.STARSET)
public final class CompanionsConfig {

    @ConfigEntry(
            comment = "The amount of charges a single altar can cap",
            min = 0, max = 1000
    )
    public static int SHADOW_ALTAR_MAX_CHARGES = 20;

    @ConfigEntry(
            comment = "The amount of blood charges a single altar can cap",
            min = 0, max = 1000
    )
    public static int SHADOW_ALTAR_BLOOD_CHARGES_AMOUNT = 4;

    @ConfigEntry(
            comment = "Should certain companions have the ability to work?"
    )
    public static boolean SHOULD_COMPANIONS_WORK = true;

    @ConfigEntry(
            comment = "Can companions wander around if either not following the owner nor sitting?"
    )
    public static boolean SHOULD_COMPANIONS_WANDER = true;

    @ConfigEntry(
            comment = "The maximum distance (in blocks) at which companions will teleport to their owner (in a valid position).",
            min = 0, max = 1000
    )
    public static int COMPANIONS_FOLLOW_OWNER_TELEPORT_DISTANCE = 25;

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
            category = "Dinamo Companion",
            comment = "The maximum number of tesla modules that can be connected in a chain without using a voltaic repeater, starting from a Dinamo. "
                    + "For example, if set to 3, the Dinamo can connect to coil 1, which connects to coil 2, which connects to coil 3.",
            note = "Any additional tesla modules beyond this limit will not be powered.",
            min = 0, max = 50
    )
    public static int DINAMO_MAX_CHAIN_CONNECTIONS = 7;


    /**
     * ANTLION
     */

    @ConfigEntry(
            category = "Antlion",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean ANTLION_KEEP_CHUNK_LOADED = true;


    /**
     * CORNELIUS
     */

    @ConfigEntry(
            category = "Cornelius",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean CORNELIUS_KEEP_CHUNK_LOADED = true;


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


    /**
     * TEDDY
     */

    @ConfigEntry(
            category = "Teddy",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean TEDDY_KEEP_CHUNK_LOADED = true;


    /**
     * SOUL MAGE
     */

    @ConfigEntry(
            category = "Soul Mage",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean SOUL_MAGE_KEEP_CHUNK_LOADED = true;


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


    /**
     * MINION
     */

    @ConfigEntry(
            category = "Minion",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean MINION_KEEP_CHUNK_LOADED = true;


    /**
     * MANKH
     */

    @ConfigEntry(
            category = "Mankh",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean MANKH_KEEP_CHUNK_LOADED = true;


    /**
     * CLOAK
     */

    @ConfigEntry(
            category = "Cloak",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean MANKH_VICAR_KEEP_CHUNK_LOADED = true;


    /**
     * PUPPET
     */

    @ConfigEntry(
            category = "Puppet",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean PUPPET_KEEP_CHUNK_LOADED = true;


    /**
     * PUPPET GLOVE
     */

    @ConfigEntry(
            category = "Puppet Glove",
            comment = "Determines whether this entity keeps its current chunk loaded. "
                    + "If false, the entity will not teleport to its owner from unloaded chunks. "
    )
    public static boolean PUPPET_GLOVE_KEEP_CHUNK_LOADED = true;


    /**
     * MAGIC BOOKS
     */

    @ConfigEntry(
            category = "Magic Books",
            comment = "The radius (in blocks) of the Fire Mark book's effect area.",
            min = 1.0D, max = 10.0D
    )
    public static double FIRE_MARK_EFFECT_RADIUS = 2.5D;

    @ConfigEntry(
            category = "Magic Books",
            comment = "The amount of HP the heal ring heals",
            min = 0.0D, max = 100.0D
    )
    public static double HEAL_RING_HEALING = 6.0D;

}