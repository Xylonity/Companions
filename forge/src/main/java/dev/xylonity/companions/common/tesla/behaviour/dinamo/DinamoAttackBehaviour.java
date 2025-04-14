package dev.xylonity.companions.common.tesla.behaviour.dinamo;

import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.entity.custom.DinamoEntity;
import dev.xylonity.companions.common.util.interfaces.ITeslaGeneratorBehaviour;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.stream.Collectors;

public class DinamoAttackBehaviour implements ITeslaGeneratorBehaviour {

    private static final int MAX_ATTACK_RADIUS = 10;

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
            Vec3 pos = dinamo.position();
            AABB searchBox = new AABB(
                    pos.x - MAX_ATTACK_RADIUS, pos.y - MAX_ATTACK_RADIUS, pos.z - MAX_ATTACK_RADIUS,
                    pos.x + MAX_ATTACK_RADIUS, pos.y + MAX_ATTACK_RADIUS, pos.z + MAX_ATTACK_RADIUS
            );

            // We search for visible monsters
            // TODO: add extra conditions
            List<LivingEntity> visible =
                    dinamo.level().getEntitiesOfClass(LivingEntity.class, searchBox, e -> {

                        if (e instanceof Player player) {
                            return !player.isCreative() && !player.isSpectator() && player.equals(dinamo.getOwner());
                        }

                        if (dinamo.getOwner() != null && dinamo.getOwner().getLastHurtMob() == e) {
                            return true;
                        }

                        if (e instanceof TamableAnimal) {
                            return false;
                        }

                        return e instanceof Monster;
                    })
                    .stream().filter(dinamo::hasLineOfSight)
                    .collect(Collectors.toList());

            dinamo.visibleEntities = visible;

            if (!visible.isEmpty()) {
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
                    for (LivingEntity target : visible) {
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
