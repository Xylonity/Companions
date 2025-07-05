package dev.xylonity.companions.common.tesla.behaviour.dinamo;

import dev.xylonity.companions.common.entity.companion.DinamoEntity;
import dev.xylonity.companions.common.util.interfaces.ITeslaGeneratorBehaviour;
import dev.xylonity.companions.registry.CompanionsParticles;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DinamoAttackBehaviour implements ITeslaGeneratorBehaviour {

    @Override
    public void tick(DinamoEntity dinamo) {

        if (dinamo.getAttackCycleCounter() == 0) {
            searchForNearbyEntities(dinamo);
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
            }

        }

        // Reset once the time is up
        if (dinamo.getAttackCycleCounter() >= DINAMO_ATTACK_DELAY) {
            dinamo.setAttackCycleCounter(0);
            dinamo.entitiesToAttack.clear();
            return;
        }

        // Up the cicle count
        dinamo.setAttackCycleCounter(dinamo.getAttackCycleCounter() + 1);
    }

    private void searchForNearbyEntities(DinamoEntity dinamo) {
        Vec3 pos = dinamo.position();
        double radius = 10;
        AABB searchBox = new AABB(
                pos.x - radius, pos.y - radius, pos.z - radius,
                pos.x + radius, pos.y + radius, pos.z + radius
        );

        // We can equal both lists since both of them are mutable. Not doing this on a separated goal to avoid networking and such
        dinamo.entitiesToAttack = dinamo.level().getEntitiesOfClass(LivingEntity.class, searchBox, e ->
            {
                if (dinamo.getOwner() != null && dinamo.getOwner().getLastHurtByMob() != null
                        && dinamo.getOwner().getLastHurtByMob().getUUID().equals(e.getUUID())) {
                    return true;
                }

                if (dinamo.getOwner() != null && dinamo.getOwner().getLastHurtMob() != null
                        && dinamo.getOwner().getLastHurtMob().getUUID().equals(e.getUUID())) {
                    return true;
                }

                if (e.getUUID().equals(dinamo.getOwnerUUID())) {
                    return false;
                }

                return e instanceof Monster;
            })
            .stream()
            .filter(dinamo::hasLineOfSight)
            .collect(Collectors.toCollection(ArrayList::new));
    }

}
