package dev.xylonity.companions.common.entity.ai.antlion.tamable.goal;

import dev.xylonity.companions.common.entity.ai.antlion.tamable.AbstractAntlionAttackGoal;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import net.minecraft.world.entity.LivingEntity;

public class AntlionSoldierAttackGoal extends AbstractAntlionAttackGoal {

    public AntlionSoldierAttackGoal(AntlionEntity antlion, int minCd, int maxCd) {
        super(antlion, 20, minCd, maxCd);
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
        return super.canUse() && antlion.getTarget() != null && antlion.distanceToSqr(antlion.getTarget()) <= 9;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        antlion.doHurtTarget(target);
    }

    @Override
    protected int attackDelay() {
        return 7;
    }

    @Override
    protected int attackType() {
        return 1;
    }

    @Override
    protected int variant() {
        return 3;
    }

}