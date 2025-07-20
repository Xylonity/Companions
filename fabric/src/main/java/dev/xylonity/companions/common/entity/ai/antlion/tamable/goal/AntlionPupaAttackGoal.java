package dev.xylonity.companions.common.entity.ai.antlion.tamable.goal;

import dev.xylonity.companions.common.entity.ai.antlion.tamable.AbstractAntlionAttackGoal;
import dev.xylonity.companions.common.entity.companion.AntlionEntity;
import net.minecraft.world.entity.LivingEntity;

public class AntlionPupaAttackGoal extends AbstractAntlionAttackGoal {

    public AntlionPupaAttackGoal(AntlionEntity antlion, int minCd, int maxCd) {
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
        return super.canUse() && antlion.getTarget() != null && antlion.distanceToSqr(antlion.getTarget()) <= 9;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        antlion.doHurtTarget(target);
    }

    @Override
    protected int attackDelay() {
        return 6;
    }

    @Override
    protected int attackType() {
        return 1;
    }

    @Override
    protected int variant() {
        return 1;
    }

}