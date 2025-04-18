package dev.xylonity.companions.config;

import dev.xylonity.companions.config.api.*;

@AutoConfig(file = "companions-main", style = DecorationType.STARSET, categoryBanner = false)
public final class CompanionsConfig {

    @ConfigEntry(category = "Dinamo Companion",
                 comment = "The maximum number of Tesla receivers that can be connected in a chain, starting from a Dinamo. " +
                         "For example, if set to 3, the Dinamo can connect to Receiver1, which connects to Receiver2, which connects to Receiver3.",
                 note = "Any additional receivers beyond this limit will not be powered.",
                 min = 0, max = 50)
    public static int DINAMO_MAX_RECEIVER_CONNECTIONS = 7;

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

}
