package dev.xylonity.companions.common.entity.ai.cornelius.summon.goal;

import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.ai.cornelius.summon.AbstractCorneliusSummonAttackGoal;
import dev.xylonity.companions.common.entity.summon.NetherBullfrogEntity;
import dev.xylonity.companions.common.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

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
        return super.canUse() && summon.getTarget() != null && summon.distanceTo(summon.getTarget()) < 3;
    }

    @Override
    protected int getAttackType() {
        return 1;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (LivingEntity e : summon.level().getEntitiesOfClass(LivingEntity.class, new AABB(summon.blockPosition()).inflate(1))) {
            if (!Util.areEntitiesLinked(e, summon) && isEntityInFront(summon, e, 180)) {
                summon.doHurtTarget(e);
            }
        }
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