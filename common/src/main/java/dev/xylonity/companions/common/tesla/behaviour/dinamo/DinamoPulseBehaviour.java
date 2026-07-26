package dev.xylonity.companions.common.tesla.behaviour.dinamo;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.entity.companion.DinamoEntity;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.common.tesla.ConnectionTarget;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.common.util.interfaces.ITeslaGeneratorBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DinamoPulseBehaviour implements ITeslaGeneratorBehaviour {

    @Override
    public void tick(DinamoEntity dinamo) {

        // Animation
        if (dinamo.getCycleCounter() < ELECTRICAL_CHARGE_DURATION) {
            dinamo.setAnimationStartTick(dinamo.getCycleCounter());
            dinamo.setActive(true);
        }
        else if (dinamo.getCycleCounter() == ELECTRICAL_CHARGE_DURATION) {
            dinamo.setActive(false);
            dinamo.setAnimationStartTick(0);
        }

        // Sends a pulse at the right tick
        if (dinamo.getCycleCounter() == TICKS_BEFORE_SENDING_PULSE) {
            for (final ConnectionTarget target : dinamo.getOutgoing()) {
                // Starts a cycle on connected block entities
                if (target.isBlock()) {
                    final BlockEntity blockEntity = dinamo.level().getBlockEntity(target.blockPos());
                    if (blockEntity instanceof AbstractTeslaBlockEntity coil) {
                        coil.startCycle();
                    }

                }

                // Hurts entities near the connection line
                final Vec3 start = dinamo.position();
                Vec3 end;

                if (target.isBlock()) {
                    end = target.blockPos().getCenter();
                }
                else {
                    final Entity entity = CompanionsEntityTracker.getEntityByUUID(target.entityId());
                    if (entity != null) {
                        end = entity.position().add(0.0, entity.getBbHeight() * 0.5D, 0.0);
                    }
                    else {
                        continue;
                    }

                }

                final List<LivingEntity> entities = dinamo.level().getEntitiesOfClass(
                        LivingEntity.class, new AABB(start, end).inflate(1.0D));

                hurtNearLine(dinamo, dinamo.level(), entities, start, end);
            }

        }

        // Resets the cycle
        if (dinamo.getCycleCounter() >= MAX_LAPSUS) {
            dinamo.setCycleCounter(0);
        }

        dinamo.setCycleCounter(dinamo.getCycleCounter() + 1);
    }

    private void hurtNearLine(DinamoEntity dinamo, Level level, List<LivingEntity> entitiesToHurt, Vec3 origin, Vec3 end) {
        for (final LivingEntity victim : entitiesToHurt) {
            if (ITeslaUtil.isEntityNearLine(origin, end, victim, 0.75D)) {
                if (level instanceof ServerLevel sv && dinamo.getOwnerUUID() != null) {
                    if (Util.areEntitiesLinked(dinamo, victim)) {
                        continue;
                    }

                    final Entity owner = sv.getEntity(dinamo.getOwnerUUID());
                    if (!Util.areEntitiesLinked(owner, victim) && !Util.areTeammates(owner, victim)) {
                        victim.hurt(victim.level().damageSources().lightningBolt(), (float) CompanionsConfig.ELECTRICITY_DAMAGE);
                        victim.addEffect(new MobEffectInstance(CompanionsEffects.ELECTROSHOCK.get(), 50, 0, false, true, true));
                    }
                }

            }

        }

    }

}