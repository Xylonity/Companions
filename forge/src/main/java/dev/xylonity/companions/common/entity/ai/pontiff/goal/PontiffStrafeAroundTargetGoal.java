package dev.xylonity.companions.common.entity.ai.pontiff.goal;

import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PontiffStrafeAroundTargetGoal extends Goal {
    private final SacredPontiffEntity mob;
    private final double speed;
    private final float radiusSqr;

    private int seeTime;
    private boolean clockwise;
    private boolean backwards;
    private int strafeTime = -1;

    public PontiffStrafeAroundTargetGoal(SacredPontiffEntity mob, double speed, float radius) {
        this.mob = mob;
        this.speed = speed;
        this.radiusSqr = (float) Math.pow(radius, 2);
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override public boolean canUse() {
        return mob.getTarget() != null && mob.getAttackType() == 0 && mob.getActivationPhase() == 2;
    }

    @Override public boolean canContinueToUse() {
        return canUse();
    }

    @Override public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        double distSqr = mob.distanceToSqr(target);
        boolean canSee = mob.getSensing().hasLineOfSight(target);

        seeTime = canSee ? Math.min(seeTime+1, 20) : seeTime-1;

        if (distSqr <= radiusSqr && seeTime >= 20) {
            mob.getNavigation().stop();
            ++strafeTime;
        } else {
            mob.getNavigation().moveTo(target, speed);
            strafeTime = -1;
        }

        if (strafeTime >= 20) {
            if (mob.getRandom().nextFloat() < 0.3) clockwise  = !clockwise;
            if (mob.getRandom().nextFloat() < 0.3) backwards  = !backwards;
            strafeTime = 0;
        }

        if (strafeTime > -1) {
            if (distSqr > radiusSqr * 0.75f) backwards = false;
            else if (distSqr < radiusSqr * 0.25f) backwards = true;

            mob.getMoveControl().strafe(backwards ? -0.5F : 0.5F, clockwise ? 0.5F  : -0.5F);
            mob.lookAt(target, 30f, 30f);
        } else {
            mob.getLookControl().setLookAt(target, 30f, 30f);
        }
    }

}