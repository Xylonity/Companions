package dev.xylonity.companions.common.entity.ai.cornelius.summon.goal;

import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.AbstractCorneliusSummonAttackGoal;
import dev.xylonity.companions.common.entity.summon.NetherBullfrogEntity;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class NetherBullfrogAirSlashGoal extends AbstractCorneliusSummonAttackGoal {

    public NetherBullfrogAirSlashGoal(CompanionSummonEntity summon, int minCd, int maxCd) {
        super(summon, 55, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        summon.setNoMovement(true);
        LivingEntity target = summon.getTarget();
        if (target != null) {
            summon.getLookControl().setLookAt(target, 30F, 30F);
        }
    }

    @Override
    public void stop() {
        super.stop();
        summon.setNoMovement(false);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && summon.getTarget() != null && summon.distanceTo(summon.getTarget()) < 3;
    }

    @Override
    public void tick() {
        LivingEntity target = summon.getTarget();
        if (attackTicks == attackDelay() && target != null && target.isAlive()) {
            performAttack(target);
        }

        if (attackTicks == attackDelay()) {
            spawnParticles();
        }

        attackTicks++;
    }

    @Override
    protected int getAttackType() {
        return 4;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (LivingEntity e : summon.level().getEntitiesOfClass(LivingEntity.class, summon.getBoundingBox().inflate(2.5))) {
            if (!Util.areEntitiesLinked(e, summon)) {
                e.hurt(summon.damageSources().mobAttack(summon), 9f);
            }
        }

        summon.playSound(CompanionsSounds.NETHER_BULLFROG_AIR_SLASH_END.get());
    }

    protected void spawnParticles() {
        if (!(summon.level() instanceof ServerLevel level)) return;

        Vec3 look = summon.getLookAngle();

        double bx = summon.getX() + look.x * 1.5;
        double by = summon.getY() + 0.15;
        double bz = summon.getZ() + look.z * 1.5;

        double spread = 0.03;
        level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, bx, by, bz, 10, look.x * spread, spread, look.z * spread, 0.0125);
    }

    @Override
    protected int attackDelay() {
        return 26;
    }

    @Override
    protected Class<? extends CompanionSummonEntity> summonType() {
        return NetherBullfrogEntity.class;
    }

}