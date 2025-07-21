package dev.xylonity.companions.common.entity.ai.antlion.wild.goal;

import dev.xylonity.companions.common.entity.ai.antlion.wild.AbstractWildAntlionAttackGoal;
import dev.xylonity.companions.common.entity.hostile.WildAntlionEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Random;

public class WildAntlionBaseAttackGoal extends AbstractWildAntlionAttackGoal {

    public WildAntlionBaseAttackGoal(WildAntlionEntity antlion, int minCd, int maxCd) {
        super(antlion, 15, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        antlion.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        antlion.setNoMovement(false);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = antlion.getTarget();
        if (target != null && antlion.distanceToSqr(target) > 9) {
            return false;
        }

        if (antlion.getState() == 0 && target != null) {
            antlion.cycleState();
            return false;
        }

        if (!antlion.isUnderground()) {
            return false;
        }

        return super.canUse();
    }

    @Override
    protected void performAttack(LivingEntity target) {
        antlion.doHurtTarget(target);
        if (antlion.level().random.nextFloat() < 0.45f) target.setRemainingFireTicks(new Random().nextInt(1, 3) * 20);
    }

    @Override
    protected int attackDelay() {
        return 7;
    }

    @Override
    protected int attackType() {
        return 1;
    }

}