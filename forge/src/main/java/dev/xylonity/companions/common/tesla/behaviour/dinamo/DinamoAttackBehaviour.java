package dev.xylonity.companions.common.tesla.behaviour.dinamo;

import dev.xylonity.companions.common.entity.custom.DinamoEntity;
import dev.xylonity.companions.common.util.interfaces.ITeslaGeneratorBehaviour;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.world.entity.LivingEntity;

public class DinamoAttackBehaviour implements ITeslaGeneratorBehaviour {

    @Override
    public void tick(DinamoEntity dinamo) {

        // Deal with animation stuff
        if (dinamo.getAttackCycleCounter() < ELECTRICAL_CHARGE_DURATION) {
            dinamo.setAnimationStartTick(dinamo.getAttackCycleCounter());
            dinamo.setActiveForAttack(true);
        }
        else if (dinamo.getAttackCycleCounter() == ELECTRICAL_CHARGE_DURATION) {
            dinamo.setActiveForAttack(false);
            dinamo.setAnimationStartTick(0);
        }

        if (dinamo.isActiveForAttack()) {

            if (!dinamo.visibleEntities.isEmpty()) {
                // Decorative particles lol
                if (dinamo.level().isClientSide()) {
                    double radius = 0.42;
                    double initialY = dinamo.position().y + dinamo.getBbHeight() - 0.60;
                    for (int i = 0; i < 360; i += 120) {
                        double angleRadians = Math.toRadians(i);
                        double particleX = dinamo.position().x + radius * Math.cos(angleRadians);
                        double particleZ = dinamo.position().z + radius * Math.sin(angleRadians);
                        dinamo.level().addParticle(CompanionsParticles.DINAMO_SPARK.get(), particleX, initialY, particleZ, 0d, 0.35d, 0d);
                    }
                }

                // Delay before hurting so it syncs with the electrical charge anim
                if (dinamo.getAttackCycleCounter() == 3) {
                    for (LivingEntity target : dinamo.visibleEntities) {
                        dinamo.doHurtTarget(target);
                    }
                }
            }
        }

        // Reset once the time is up
        if (dinamo.getAttackCycleCounter() >= DINAMO_ATTACK_DELAY) {
            dinamo.setAttackCycleCounter(0);
        }

        // Up the cicle count
        dinamo.setAttackCycleCounter(dinamo.getAttackCycleCounter() + 1);
    }

}
