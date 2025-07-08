package dev.xylonity.companions.common.tesla.behaviour.dinamo;

import dev.xylonity.companions.common.entity.companion.DinamoEntity;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.common.util.interfaces.ITeslaGeneratorBehaviour;
import dev.xylonity.companions.registry.CompanionsParticles;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DinamoAttackBehaviour implements ITeslaGeneratorBehaviour {

    @Override
    public void tick(DinamoEntity dinamo) {

        if (dinamo.getAttackCycleCounter() == 0) {
            searchForTargets(dinamo);
        }

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

            if (!dinamo.entitiesToAttack.isEmpty()) {
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

                // Delay before hurting, so it syncs with the electrical charge anim
                if (dinamo.getAttackCycleCounter() == 3) {
                    for (LivingEntity target : dinamo.entitiesToAttack) {
                        dinamo.doHurtTarget(target);
                    }
                }

                if (dinamo.getAttackCycleCounter() == 0) dinamo.playSound(CompanionsSounds.DINAMO_ATTACK.get(), 0.45f, 1f);
            }

        }

        // Reset once the time is up
        if (dinamo.getAttackCycleCounter() >= DINAMO_ATTACK_DELAY) {
            dinamo.setAttackCycleCounter(0);
            dinamo.entitiesToAttack.clear();
            dinamo.setTargetIds("");
            return;
        }

        // Up the cicle count
        dinamo.setAttackCycleCounter(dinamo.getAttackCycleCounter() + 1);
    }

    private void searchForTargets(DinamoEntity dinamo) {
        List<LivingEntity> list = dinamo.level().getEntitiesOfClass(LivingEntity.class, dinamo.getBoundingBox().inflate(12), e ->
            {
                if (Util.areEntitiesLinked(e, dinamo)) return false;

                return e instanceof Monster;
            })
            .stream()
            .filter(dinamo::hasLineOfSight)
            .collect(Collectors.toCollection(ArrayList::new));

        for (LivingEntity m : list) {
            if (!dinamo.entitiesToAttack.contains(m)) {
                dinamo.entitiesToAttack.add(m);
                dinamo.setTargetIds(dinamo.getTargetIds() + m.getId() + ";");
            }
        }

        if (dinamo.getTarget() != null) {
            dinamo.entitiesToAttack.add(dinamo.getTarget());
            dinamo.setTargetIds(dinamo.getTargetIds() + dinamo.getTarget().getId() + ";");
        }

    }

}