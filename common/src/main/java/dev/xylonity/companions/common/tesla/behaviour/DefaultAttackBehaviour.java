package dev.xylonity.companions.common.tesla.behaviour;

import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.common.tesla.ConnectionTarget;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.common.util.interfaces.ITeslaNodeBehaviour;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import dev.xylonity.companions.config.CompanionsConfig;
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

    @Override
    public void process(AbstractTeslaBlockEntity module, Level level, BlockPos blockPos, BlockState blockState) {
        if (!module.isActive()) {
            return;
        }

        for (final ConnectionTarget target : module.getOutgoing()) {
            final Vec3 start = module.getBlockPos().getCenter();
            Vec3 end;
            if (target.isEntity()) {
                final Entity entity = CompanionsEntityTracker.getEntityByUUID(target.entityId());
                if (entity == null) {
                    continue;
                }

                end = entity.position().add(0.0, entity.getBbHeight() * 0.5D, 0.0);
            }
            else if (target.isBlock()) {
                end = target.blockPos().getCenter();
            }
            else {
                continue;
            }

            List<LivingEntity> entities =
                    level.getEntitiesOfClass(LivingEntity.class, new AABB(start, end).inflate(1.0D));

            hurtNearLine(module, level, entities, start, end);
        }

    }

    private void hurtNearLine(AbstractTeslaBlockEntity module, Level level, List<LivingEntity> entitiesToHurt, Vec3 origin, Vec3 end) {
        for (final LivingEntity victim : entitiesToHurt) {
            if (ITeslaUtil.isEntityNearLine(origin, end, victim, 0.75D)) {
                if (level instanceof ServerLevel serverLevel && module.getOwnerUUID() != null) {
                    if (!Util.areEntitiesLinked(serverLevel.getEntity(module.getOwnerUUID()), victim)) {
                        victim.hurt(victim.level().damageSources().lightningBolt(), (float) CompanionsConfig.ELECTRICITY_DAMAGE);
                        victim.addEffect(new MobEffectInstance(CompanionsEffects.ELECTROSHOCK.get(), 50, 0, false, true, true));
                    }

                }

            }

        }

    }

}