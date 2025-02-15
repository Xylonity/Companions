package dev.xylonity.companions.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CompanionsForgeConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // Dinamo
    public static final ForgeConfigSpec.IntValue DINAMO_MAX_RECEIVER_CONNECTIONS;
    public static final ForgeConfigSpec.BooleanValue DINAMO_RECEIVER_REDSTONE_MODE;
    public static final ForgeConfigSpec.IntValue DINAMO_RAY_CHARGE_INTERVAL;

    // Illager Golem
    public static final ForgeConfigSpec.IntValue ILLAGER_GOLEM_ATTACK_RANGE;
    public static final ForgeConfigSpec.IntValue ILLAGER_GOLEM_ATTACK_INTERVAL;

    // Magic Books
    public static final ForgeConfigSpec.DoubleValue FIRE_MARK_EFFECT_RADIUS;

    static {

        BUILDER.push("Dinamo Configuration");

        DINAMO_MAX_RECEIVER_CONNECTIONS = BUILDER
                .comment("The maximum number of Tesla receivers that can be connected in a chain, starting from a Dinamo.")
                .comment("For example, if set to 3, the Dinamo can connect to Receiver1, which connects to Receiver2, which connects to Receiver3.")
                .comment("Any additional receivers beyond this limit will not be powered.")
                .defineInRange("dinamoMaxReceiverConnections", 3, 0, 50);
        DINAMO_RECEIVER_REDSTONE_MODE = BUILDER
                .comment("Determines how the Tesla Receiver interacts with Redstone circuits when powered by a Dinamo.")
                .comment("If true, the receiver emits a Redstone clock signal (pulses when receiving energy).")
                .comment("If false, the receiver emits a continuous Redstone signal (stays active as long as it is powered).")
                .define("dinamoReceiverRedstoneMode", true);
        DINAMO_RAY_CHARGE_INTERVAL = BUILDER
                .comment("The time delay (in ticks) between electric charge transmissions from one node to another.")
                .comment("For reference, 20 ticks = 1 second.")
                .defineInRange("dinamoRayChargeInterval", 10, 9, 200);

        BUILDER.pop();


        BUILDER.push("Illager Golem Configuration");

        ILLAGER_GOLEM_ATTACK_RANGE = BUILDER
                .comment("The maximum attack range (in blocks) of the Illager Golem.")
                .defineInRange("illagerGolemAttackRange", 6, 1, 40);
        ILLAGER_GOLEM_ATTACK_INTERVAL = BUILDER
                .comment("The time delay (in ticks) between consecutive attacks of the Illager Golem.")
                .comment("For reference, 20 ticks = 1 second.")
                .defineInRange("illagerGolemAttackInterval", 60, 10, 5000);

        BUILDER.pop();


        BUILDER.push("Magic Books Configuration");

        FIRE_MARK_EFFECT_RADIUS = BUILDER
                .comment("The radius (in blocks) of the Fire Mark book's effect area.")
                .defineInRange("fireMarkEffectRadius", 2.5, 1.0, 10.0);

        BUILDER.pop();


        SPEC = BUILDER.build();
    }

}