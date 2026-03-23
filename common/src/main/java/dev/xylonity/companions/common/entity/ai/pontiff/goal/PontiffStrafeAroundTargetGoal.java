package dev.xylonity.companions.common.entity.ai.pontiff.goal;

import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PontiffStrafeAroundTargetGoal extends Goal {
    private final SacredPontiffEntity pontiff;
    private final double speed;
    private final float radiusSqr;

    private int seeTime;
    private boolean clockwise;
    private boolean backwards;
    private int strafeTime = -1;

    public PontiffStrafeAroundTargetGoal(SacredPontiffEntity mob, double speed, float radius) {
        this.pontiff = mob;
        this.speed = speed;
        this.radiusSqr = (float) Math.pow(radius, 2);
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (pontiff.getTarget() == null) return false;
        if (pontiff.getAttackType() != 0) return false;
        if (!pontiff.shouldSearchTarget()) return false;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = pontiff.getTarget();
        if (target == null) return;

        double distSqr = pontiff.distanceToSqr(target);
        boolean canSee = pontiff.getSensing().hasLineOfSight(target);

        seeTime = canSee ? Math.min(seeTime+1, 20) : seeTime-1;

        if (distSqr <= radiusSqr && seeTime >= 20) {
            pontiff.getNavigation().stop();
            ++strafeTime;
        } else {
            pontiff.getNavigation().moveTo(target, speed);
            strafeTime = -1;
        }

        if (strafeTime >= 20) {
            if (pontiff.getRandom().nextFloat() < 0.3) clockwise  = !clockwise;
            if (pontiff.getRandom().nextFloat() < 0.3) backwards  = !backwards;
            strafeTime = 0;
        }

        if (strafeTime > -1) {
            if (distSqr > radiusSqr * 0.75f) backwards = false;
            else if (distSqr < radiusSqr * 0.25f) backwards = true;

            pontiff.getMoveControl().strafe(backwards ? -0.5F : 0.5F, clockwise ? 0.5F  : -0.5F);

            if (!pontiff.onGround()) pontiff.setDeltaMovement(pontiff.getDeltaMovement().x, pontiff.getDeltaMovement().y - 0.3, pontiff.getDeltaMovement().z);

            if (pontiff.shouldLookAtTarget()) pontiff.lookAt(target, 30f, 30f);
        } else {
            if (pontiff.shouldLookAtTarget()) pontiff.getLookControl().setLookAt(target, 30f, 30f);
        }
    }

}