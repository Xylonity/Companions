package dev.xylonity.companions.common.tesla.behaviour.dinamo;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.entity.companion.DinamoEntity;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.common.util.interfaces.ITeslaGeneratorBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
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
import java.util.Set;

public class DinamoPulseBehaviour implements ITeslaGeneratorBehaviour {

    @Override
    public void tick(DinamoEntity dinamo) {

        // Deal with animation stuff
        if (dinamo.getCycleCounter() < ELECTRICAL_CHARGE_DURATION) {
            dinamo.setAnimationStartTick(dinamo.getCycleCounter());
            dinamo.setActive(true);
        }
        else if (dinamo.getCycleCounter() == ELECTRICAL_CHARGE_DURATION) {
            dinamo.setActive(false);
            dinamo.setAnimationStartTick(0);
        }

        if (dinamo.getCycleCounter() ==  TICKS_BEFORE_SENDING_PULSE){
            // Starts the cycle for all the nodes around it
            Set<TeslaConnectionManager.ConnectionNode> nodes = TeslaConnectionManager.getInstance().getOutgoing(dinamo.asConnectionNode());
            for (TeslaConnectionManager.ConnectionNode node : nodes) {
                if (!node.isEntity()) {
                    BlockEntity be = dinamo.level().getBlockEntity(node.blockPos());
                    if (be instanceof AbstractTeslaBlockEntity coil) {
                        coil.startCycle();
                    }
                }

                Vec3 start = dinamo.position();
                Vec3 end;

                if (node.isBlock()) {
                    end = node.blockPos().getCenter();
                } else {
                    Entity entity = CompanionsEntityTracker.getEntityByUUID(node.entityId());
                    if (entity != null) {
                        end = entity.position().add(0.0, entity.getBbHeight() * 0.5D, 0.0);
                    } else {
                        return;
                    }
                }

                List<LivingEntity> entities = dinamo.level().getEntitiesOfClass(LivingEntity.class, new AABB(start, end).inflate(1.0D));

                hurtNearLine(dinamo, dinamo.level(), entities, start, end);
            }
        }

        // Reset once the time is up
        if (dinamo.getCycleCounter() >= MAX_LAPSUS) {
            dinamo.setCycleCounter(0);
        }

        //Up the cicle count
        dinamo.setCycleCounter(dinamo.getCycleCounter() + 1);
    }

    private void hurtNearLine(DinamoEntity dinamo, Level level, List<LivingEntity> entitiesToHurt, Vec3 origin, Vec3 end) {
        for (LivingEntity victim : entitiesToHurt) {
            if (ITeslaUtil.isEntityNearLine(origin, end, victim, 0.75D)) {
                if (level instanceof ServerLevel sv && dinamo.getOwnerUUID() != null) {
                    if (!Util.areEntitiesLinked(sv.getEntity(dinamo.getOwnerUUID()), victim)) {
                        victim.hurt(victim.level().damageSources().lightningBolt(), 7f);
                        victim.addEffect(new MobEffectInstance(CompanionsEffects.ELECTROSHOCK.get(), 50, 0, false, true, true));
                    }
                }
            }
        }

    }

}
