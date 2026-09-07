package dev.xylonity.companions.common.entity.ai.generic;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

import java.util.EnumSet;

public class MobApproachTargetGoal extends Goal {

    private final Mob mob;
    private final PathNavigation navigation;
    private final double speedModifier;
    private final double startDistanceSqr;
    private final double stopDistanceSqr;
    private LivingEntity target;
    private int timeToRecalculatePath;

    public MobApproachTargetGoal(Mob mob, double speedModifier, double startDistance, double stopDistance) {
        this.mob = mob;
        this.navigation = mob.getNavigation();
        this.speedModifier = speedModifier;
        this.startDistanceSqr = startDistance * startDistance;
        this.stopDistanceSqr = stopDistance * stopDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        final LivingEntity currentTarget = mob.getTarget();
        if (currentTarget == null || !currentTarget.isAlive() || mob.distanceToSqr(currentTarget) <= startDistanceSqr) {
            return false;
        }

        target = currentTarget;

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive() && mob.getTarget() == target && mob.distanceToSqr(target) > stopDistanceSqr;
    }

    @Override
    public void start() {
        timeToRecalculatePath = 0;
    }

    @Override
    public void stop() {
        target = null;
        navigation.stop();
    }

    @Override
    public void tick() {
        if (target == null) {
            return;
        }

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (--timeToRecalculatePath <= 0) {
            timeToRecalculatePath = adjustedTickDelay(10);
            navigation.moveTo(target, speedModifier);
        }

    }

}
