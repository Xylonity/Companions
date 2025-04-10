package dev.xylonity.companions.common.tesla.behaviour;

import dev.xylonity.companions.common.entity.custom.DinamoEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.interfaces.ITeslaGeneratorBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class DinamoCurrentPulseBehaviour implements ITeslaGeneratorBehaviour, ITeslaUtil {

    @Override
    public void tick(DinamoEntity generator) {
        int tickCount = generator.getTickCount();
        // Activate at the beginning of the cycle.
        if (tickCount % DinamoEntity.TIME_PER_ACTIVATION == 0) {
            generator.setActive(true);
            generator.setAnimationStartTick(tickCount);
        }
        // Deactivate after the active window.
        if (generator.isActive() && (tickCount % DinamoEntity.TIME_PER_ACTIVATION) >= DinamoEntity.ELECTRICAL_CHARGE_DURATION) {
            generator.setActive(false);
        }

        // Propagate current along outgoing connections when active.
        if (generator.isActive() && generator.level() instanceof ServerLevel level) {
            for (TeslaConnectionManager.ConnectionNode connectionNode : TeslaConnectionManager.getInstance().getOutgoing(generator.asConnectionNode())) {
                if (connectionNode.isEntity()) {
                    Entity connectedEntity = level.getEntity(connectionNode.entityId());
                    if (connectedEntity != null) {
                        Vec3 start = generator.position().add(0.0, generator.getBbHeight() * 0.5D, 0.0);
                        Vec3 end = connectedEntity.position().add(0.0, connectedEntity.getBbHeight() * 0.5D, 0.0);
                        AABB segmentAABB = new AABB(start, end).inflate(1.0D);
                        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, segmentAABB,
                                e -> e != generator && e != connectedEntity && e.isAlive());
                        for (LivingEntity victim : nearbyEntities) {
                            if (ITeslaUtil.isEntityNearLine(start, end, victim, 0.75D)) {
                                generator.doHurtTarget(victim);
                                if (new Random().nextFloat() <= 0.2f) {
                                    victim.addEffect(new MobEffectInstance(CompanionsEffects.ELECTROSHOCK.get(), 50, 0));
                                }
                            }
                        }
                    }
                } else if (connectionNode.isBlock()) {
                    Vec3 start = generator.position().add(0.0, generator.getBbHeight() * 0.5D, 0.0);
                    Vec3 end = connectionNode.blockPos().getCenter().add(0.0, 0.3D, 0.0);
                    AABB segmentAABB = new AABB(start, end).inflate(1.0D);
                    List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, segmentAABB,
                            e -> e != generator && e.isAlive());
                    for (LivingEntity victim : nearbyEntities) {
                        if (ITeslaUtil.isEntityNearLine(start, end, victim, 0.75D)) {
                            generator.doHurtTarget(victim);
                            if (new Random().nextFloat() <= 0.2f) {
                                victim.addEffect(new MobEffectInstance(CompanionsEffects.ELECTROSHOCK.get(), 50, 0));
                            }
                        }
                    }
                }
            }
        }
    }

}
