package dev.xylonity.companions.common.tesla.behaviour;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DefaultAttackBehaviour implements ITeslaNodeBehaviour {

    // Generic attack behaviour that almost every node can use.
    @Override
    public void process(AbstractTeslaBlockEntity module, Level level, BlockPos blockPos, BlockState blockState) {
        if (module.isActive()) {
            // For each outgoing connection
            for (TeslaConnectionManager.ConnectionNode connectionNode : TeslaConnectionManager.getInstance().getOutgoing(module.asConnectionNode())) {
                if (connectionNode.isEntity()) {
                    Entity connectedEntity = CompanionsEntityTracker.getEntityByUUID(connectionNode.entityId());
                    if (connectedEntity != null) {
                        // We create a line between this module and the outgoing node
                        Vec3 start = module.getBlockPos().getCenter();
                        Vec3 end = connectedEntity.position().add(0.0, connectedEntity.getBbHeight() * 0.5D, 0.0);

                        List<LivingEntity> entities =
                                level.getEntitiesOfClass(LivingEntity.class, new AABB(start, end).inflate(1.0D));

                        hurtNearLine(module, level, entities, start, end);
                    }

                } else if (connectionNode.isBlock()) {
                    // We create a line between this module and the outgoing node
                    Vec3 start = module.getBlockPos().getCenter();
                    Vec3 end = connectionNode.blockPos().getCenter();

                    List<LivingEntity> entities =
                            level.getEntitiesOfClass(LivingEntity.class, new AABB(start, end).inflate(1.0D));

                    hurtNearLine(module, level, entities, start, end);
                }
            }
        }

    }

    private void hurtNearLine(AbstractTeslaBlockEntity module, Level level, List<LivingEntity> entitiesToHurt, Vec3 origin, Vec3 end) {
        for (LivingEntity victim : entitiesToHurt) {
            if (ITeslaUtil.isEntityNearLine(origin, end, victim, 0.75D)) {
                if (level instanceof ServerLevel sv && module.getOwnerUUID() != null) {
                    if (!Util.areEntitiesLinked(sv.getEntity(module.getOwnerUUID()), victim)) {
                        victim.hurt(victim.level().damageSources().lightningBolt(), 7f);
                        victim.addEffect(new MobEffectInstance(CompanionsEffects.ELECTROSHOCK.get(), 50, 0, false, true, true));
                    }
                }
            }
        }

    }

}
