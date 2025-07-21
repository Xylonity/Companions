package dev.xylonity.companions.common.entity.ai.cornelius.goal;

import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import dev.xylonity.companions.common.entity.SummonFrogEntity;
import dev.xylonity.companions.common.util.interfaces.IFrogJumpUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

import java.util.EnumSet;

public class SummonHopToTargetGoal<T extends CompanionSummonEntity & IFrogJumpUtil> extends Goal {

    private final T summon;
    private final PathNavigation navigation;
    private final double speedModifier;
    private LivingEntity target;
    private int cycleCounter;

    public SummonHopToTargetGoal(T summon, double speedModifier) {
        this.summon = summon;
        this.navigation = summon.getNavigation();
        this.speedModifier = speedModifier;
        this.cycleCounter = 0;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity currentTarget = this.summon.getTarget();
        if (currentTarget == null) return false;
        if (currentTarget.isSpectator()) return false;
        if (this.summon.getAttackType() != 0) return false;

        this.target = currentTarget;
        return true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.cycleCounter = 0;
        this.summon.setCanAttack(false);
    }

    @Override
    public void stop() {
        this.navigation.stop();
        this.summon.setCanAttack(true);
        this.target = null;
    }

    @Override
    public void tick() {
        this.summon.getLookControl().setLookAt(this.target, 10.0F, (float)this.summon.getMaxHeadXRot());

        if (((SummonFrogEntity) summon).getCycleCount() == -1) ((SummonFrogEntity) summon).setCycleCount(((SummonFrogEntity) summon).getCycleCount() + 1);
        this.navigation.moveTo(this.target, this.speedModifier);

        this.cycleCounter = (this.cycleCounter + 1) % 20;
    }

}
