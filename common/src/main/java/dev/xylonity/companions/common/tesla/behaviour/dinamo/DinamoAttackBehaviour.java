package dev.xylonity.companions.common.tesla.behaviour.dinamo;

import dev.xylonity.companions.common.entity.companion.DinamoEntity;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.common.util.interfaces.ITeslaGeneratorBehaviour;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEffects;
import dev.xylonity.companions.registry.CompanionsParticles;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.effect.MobEffectInstance;
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
                // Decorative particles
                if (dinamo.level().isClientSide()) {
                    final double radius = 0.42;
                    final double initialY = dinamo.position().y + dinamo.getBbHeight() - 0.60;
                    for (int i = 0; i < 360; i += 120) {
                        final double angle = Math.toRadians(i);
                        final double px = dinamo.position().x + radius * Math.cos(angle);
                        final double pz = dinamo.position().z + radius * Math.sin(angle);
                        dinamo.level().addParticle(CompanionsParticles.DINAMO_SPARK.get(), px, initialY, pz, 0, 0.35, 0);
                    }
                }

                if (dinamo.getAttackCycleCounter() == 3) {
                    for (final LivingEntity target : dinamo.entitiesToAttack) {
                        if (target.distanceToSqr(target) <= 64) {
                            target.hurt(dinamo.damageSources().lightningBolt(), (float) CompanionsConfig.ELECTRICITY_DAMAGE);
                            if (target.getRandom().nextFloat() < 0.4f) {
                                target.addEffect(new MobEffectInstance(CompanionsEffects.holder(CompanionsEffects.ELECTROSHOCK), 50, 0, false, true, true));
                            }
                        }

                    }

                }

                if (dinamo.getAttackCycleCounter() == 0) {
                    dinamo.playSound(CompanionsSounds.DINAMO_ATTACK.get(), 0.45f, 1f);
                }

            }

        }

        if (dinamo.getAttackCycleCounter() >= dinamo.scaleAttackCooldown(DINAMO_ATTACK_DELAY)) {
            dinamo.setAttackCycleCounter(0);
            dinamo.entitiesToAttack.clear();
            dinamo.setTargetIds("");
            return;
        }

        dinamo.setAttackCycleCounter(dinamo.getAttackCycleCounter() + 1);
    }

    private boolean canBeAttacked(DinamoEntity dinamo, LivingEntity entity) {
        return !Util.areEntitiesLinked(entity, dinamo) && !Util.areTeammates(dinamo.getOwner(), entity);
    }

    private void searchForTargets(DinamoEntity dinamo) {
        final List<LivingEntity> list = dinamo.level().getEntitiesOfClass(LivingEntity.class,
                        dinamo.getBoundingBox().inflate(10),
                        entity -> canBeAttacked(dinamo, entity) && entity instanceof Monster)
                .stream()
                .filter(dinamo::hasLineOfSight)
                .collect(Collectors.toCollection(ArrayList::new));

        for (final LivingEntity livingEntity : list) {
            if (!dinamo.entitiesToAttack.contains(livingEntity)) {
                dinamo.entitiesToAttack.add(livingEntity);
                dinamo.setTargetIds(dinamo.getTargetIds() + livingEntity.getId() + ";");
            }

        }

        if (dinamo.getTarget() != null && canBeAttacked(dinamo, dinamo.getTarget())) {
            dinamo.entitiesToAttack.add(dinamo.getTarget());
            dinamo.setTargetIds(dinamo.getTargetIds() + dinamo.getTarget().getId() + ";");
        }

    }

}
