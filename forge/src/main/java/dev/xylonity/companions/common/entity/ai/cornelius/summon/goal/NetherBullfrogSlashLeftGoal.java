package dev.xylonity.companions.common.entity.ai.cornelius.summon.goal;

import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.AbstractCorneliusSummonAttackGoal;
import dev.xylonity.companions.common.entity.summon.NetherBullfrogEntity;
import dev.xylonity.companions.common.util.Util;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.entity.LivingEntity;

public class NetherBullfrogSlashLeftGoal extends AbstractCorneliusSummonAttackGoal {

    public NetherBullfrogSlashLeftGoal(CompanionSummonEntity summon, int minCd, int maxCd) {
        super(summon, 21, minCd, maxCd);
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
        return super.canUse() && summon.getTarget() != null && summon.distanceToSqr(summon.getTarget()) < 4.5;
    }

    @Override
    protected int getAttackType() {
        return 1;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (LivingEntity e : summon.level().getEntitiesOfClass(LivingEntity.class, summon.getBoundingBox().inflate(2.5))) {
            if (!Util.areEntitiesLinked(e, summon) && isEntityInFront(summon, e, 220)) {
                summon.doHurtTarget(e);
            }
        }

        summon.playSound(CompanionsSounds.NETHER_BULLFROG_SLASH.get());
    }

    @Override
    protected int attackDelay() {
        return 11;
    }

    @Override
    protected Class<? extends CompanionSummonEntity> summonType() {
        return NetherBullfrogEntity.class;
    }

}