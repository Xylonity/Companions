package dev.xylonity.companions.common.entity.ai.cornelius.summon.goal;

import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.AbstractCorneliusSummonAttackGoal;
import dev.xylonity.companions.common.entity.summon.NetherBullfrogEntity;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.entity.LivingEntity;

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
            summon.playSound(CompanionsSounds.NETHER_BULLFROG_SLASH.get());
        }

        attackTicks++;
    }

    @Override
    protected int getAttackType() {
        return 3;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (LivingEntity e : summon.level().getEntitiesOfClass(LivingEntity.class, summon.getBoundingBox().inflate(2.5))) {
            if (!Util.areEntitiesLinked(e, summon)) {
                e.hurt(summon.damageSources().mobAttack(summon), 4f);
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