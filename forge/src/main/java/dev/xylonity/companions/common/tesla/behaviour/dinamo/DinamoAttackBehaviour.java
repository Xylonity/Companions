package dev.xylonity.companions.common.tesla.behaviour.dinamo;

import dev.xylonity.companions.common.entity.custom.DinamoEntity;
import dev.xylonity.companions.common.util.interfaces.ITeslaGeneratorBehaviour;

/**
 * Attack behavior for a non–sitting (active) DinamoEntity.
 * It pulses every ATTACK_TIME_PER_ACTIVATION (60 ticks) for an 8–tick activation window.
 * During the pulse it searches for nearby targets and, on the client side, spawns particles;
 * on the server side, it deals damage/effects if appropriate.
 */
public class DinamoAttackBehaviour implements ITeslaGeneratorBehaviour {

    @Override
    public void tick(DinamoEntity generator) {
        //int tickCount = generator.getTickCount();
        //// Activate attack mode periodically if attacking is allowed.
        //if (tickCount % DinamoEntity.ATTACK_TIME_PER_ACTIVATION == 0 && generator.shouldAttack()) {
        //    generator.setActiveForAttack(true);
        //    generator.setAnimationStartTick(tickCount);
        //}
        //if (generator.isActiveForAttack() && (tickCount % DinamoEntity.ATTACK_TIME_PER_ACTIVATION) >= DinamoEntity.ELECTRICAL_CHARGE_DURATION && generator.shouldAttack()) {
        //    generator.setActiveForAttack(false);
        //    generator.visibleEntities.clear();
        //}
        //if (generator.isActiveForAttack() && generator.shouldAttack()) {
        //    Vec3 currentPosition = generator.position();
        //    AABB searchBox = new AABB(
        //            currentPosition.x - DinamoEntity.MAX_RADIUS, currentPosition.y - DinamoEntity.MAX_RADIUS, currentPosition.z - DinamoEntity.MAX_RADIUS,
        //            currentPosition.x + DinamoEntity.MAX_RADIUS, currentPosition.y + DinamoEntity.MAX_RADIUS, currentPosition.z + DinamoEntity.MAX_RADIUS
        //    );
        //    List<LivingEntity> visible = generator.level().getEntitiesOfClass(LivingEntity.class, searchBox, entity -> {
        //                if (entity instanceof Player player) {
        //                    return !player.isCreative() && !player.isSpectator() && player.equals(generator.getOwner());
        //                }
        //                return entity instanceof LivingEntity;
        //            }).stream().filter(generator::hasLineOfSight)
        //            .collect(Collectors.toList());
//
        //    generator.visibleEntities = visible;
//
        //    if (!visible.isEmpty()) {
        //        // On the client, spawn particles to visualize the attack pulse.
        //        if (generator.level().isClientSide()) {
        //            double radius = 0.42;
        //            double centerX = generator.position().x;
        //            double centerZ = generator.position().z;
        //            double initialY = generator.position().y + generator.getBbHeight() - 0.60;
        //            for (int i = 0; i < 360; i += 120) {
        //                double angleRadians = Math.toRadians(i);
        //                double particleX = centerX + radius * Math.cos(angleRadians);
        //                double particleZ = centerZ + radius * Math.sin(angleRadians);
        //                generator.level().addParticle(CompanionsParticles.DINAMO_SPARK.get(), particleX, initialY, particleZ, 0d, 0.35d, 0d);
        //            }
        //        }
        //        int elapsed = tickCount - generator.getAnimationStartTick();
        //        if (!generator.level().isClientSide() && elapsed >= 3 && elapsed < DinamoEntity.ELECTRICAL_CHARGE_DURATION) {
        //            for (LivingEntity target : visible) {
        //                generator.doHurtTarget(target);
        //            }
        //        }
        //    }
        //}
    }

}
