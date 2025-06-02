package dev.xylonity.companions.config;

import dev.xylonity.companions.config.api.*;

@AutoConfig(file = "companions-common", style = DecorationType.STARSET, categoryBanner = false)
public final class CompanionsConfig {

    @ConfigEntry(category = "Dinamo Companion",
                 comment = "The maximum number of tesla modules that can be connected in a chain without using a voltaic repeater, starting from a Dinamo. " +
                         "For example, if set to 3, the Dinamo can connect to coil 1, which connects to coil 2, which connects to coil 3.",
                 note = "Any additional tesla modules beyond this limit will not be powered.",
                 min = 0, max = 50)
    public static int DINAMO_MAX_CHAIN_CONNECTIONS = 7;

    @ConfigEntry(category = "Croissant Dragon Companion",
            comment = "The amount of time the croissant dragon needs to hatch.",
            note = "The time is measured in ticks",
            min = 10, max = 100000)
    public static int CROISSANT_EGG_LIFETIME = 6000;

    @ConfigEntry(category = "Magic Books",
            comment = "The radius (in blocks) of the Fire Mark book's effect area.",
            min = 1.0, max = 10.0)
    public static double FIRE_MARK_EFFECT_RADIUS = 2.5;

    @ConfigEntry(category = "Magic Books",
            comment = "The amount of HP the heal ring heals",
            min = 0.0F, max = 100.0F)
    public static float HEAL_RING_HEALING = 6.0F;

    @ConfigEntry(category = "Shadow",
            comment = "The amount of charges a single altar can cap",
            min = 0, max = 1000)
    public static int SHADOW_ALTAR_MAX_CHARGES = 20;

    @ConfigEntry(category = "Shadow",
            comment = "The amount of blood charges a single altar can cap",
            min = 0, max = 1000)
    public static int SHADOW_ALTAR_BLOOD_CHARGES_AMOUNT = 4;

    @ConfigEntry(comment = "Should certain companions have the ability to work?")
    public static boolean SHOULD_COMPANIONS_WORK = true;

    @ConfigEntry(comment = "Can companions wander around if either not following the owner nor sitting?")
    public static boolean SHOULD_COMPANIONS_WANDER = true;

}
