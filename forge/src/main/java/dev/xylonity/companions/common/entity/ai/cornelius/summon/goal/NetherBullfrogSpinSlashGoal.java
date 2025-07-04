package dev.xylonity.companions.common.entity.ai.cornelius.summon.goal;

import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.AbstractCorneliusSummonAttackGoal;
import dev.xylonity.companions.common.entity.summon.NetherBullfrogEntity;
import dev.xylonity.companions.common.util.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NetherBullfrogSpinSlashGoal extends AbstractCorneliusSummonAttackGoal {

    public NetherBullfrogSpinSlashGoal(CompanionSummonEntity summon, int minCd, int maxCd) {
        super(summon, 41, minCd, maxCd);
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
        if (target != null) {
            summon.getLookControl().setLookAt(target, 30F, 30F);
        }

        if (attackTicks > 10 && attackTicks % 10 == 0 && attackTicks < 33 && target != null && target.isAlive()) {
            performAttack(target);
        }

        attackTicks++;
    }

    @Override
    protected int getAttackType() {
        return 3;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (LivingEntity e : summon.level().getEntitiesOfClass(LivingEntity.class, new AABB(summon.blockPosition()).inflate(1))) {
            if (!Util.areEntitiesLinked(e, summon) && isEntityInFront(summon, e, 120)) {
                e.hurt(summon.damageSources().mobAttack(summon), 3f);
            }
        }
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