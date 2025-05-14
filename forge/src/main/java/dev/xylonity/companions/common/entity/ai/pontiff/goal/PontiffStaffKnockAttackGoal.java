package dev.xylonity.companions.common.entity.ai.pontiff.goal;

import dev.xylonity.companions.common.entity.ai.pontiff.AbstractSacredPontiffAttackGoal;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import net.minecraft.world.entity.LivingEntity;

public class PontiffStaffKnockAttackGoal extends AbstractSacredPontiffAttackGoal {

    public PontiffStaffKnockAttackGoal(SacredPontiffEntity boss, int minCd, int maxCd) {
        super(boss, 31, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        pontiff.setNoMovement(true);
    }

    @Override
    public void stop() {
        super.stop();
        pontiff.setNoMovement(false);
    }

    @Override
    protected int getAttackType() {
        return 1;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        ;;
    }

    @Override
    protected int attackDelay() {
        return 26;
    }

}