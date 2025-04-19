package dev.xylonity.companions.common.entity.ai.teddy;

import dev.xylonity.companions.common.entity.custom.TeddyEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MutatedTeddyChargeAttackGoal extends Goal {

    private enum AttackState {
        WIND_UP,
        CHARGING,
        RETREAT
    }

    private final TeddyEntity teddy;
    private LivingEntity target;

    private final double chargeSpeed;
    private final double retreatSpeed;
    private final int windUpDuration;
    private final int retreatDuration;
    private final double hitRange;
    private final double retreatDistance;

    private int stateTicks;
    private AttackState state = AttackState.WIND_UP;

    public MutatedTeddyChargeAttackGoal(TeddyEntity teddy, double chargeSpeed, double retreatSpeed, int windUpDuration, int retreatDuration, double hitRange, double retreatDistance) {
        this.teddy = teddy;
        this.chargeSpeed = chargeSpeed;
        this.retreatSpeed = retreatSpeed;
        this.windUpDuration = windUpDuration;
        this.retreatDuration = retreatDuration;
        this.hitRange = hitRange;
        this.retreatDistance = retreatDistance;

        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.teddy.getPhase() != 2 || this.teddy.getMainAction() == 0) {
            return false;
        }

        LivingEntity possibleTarget = this.teddy.getTarget();
        if (possibleTarget == null || !possibleTarget.isAlive()) {
            return false;
        }

        this.target = possibleTarget;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.teddy.getPhase() != 2 || this.teddy.getMainAction() == 0) {
            return false;
        }

        return this.target != null && this.target.isAlive();
    }

    @Override
    public void start() {
        this.state = AttackState.WIND_UP;
        this.stateTicks = 0;
        this.teddy.setNoGravity(true);
    }

    @Override
    public void stop() {
        this.target = null;
        this.state = AttackState.WIND_UP;
        this.stateTicks = 0;
    }

    @Override
    public void tick() {
        if (this.target == null) return;

        if (!this.target.isAlive()) {
            this.teddy.setTarget(null);
            return;
        }

        this.teddy.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        switch (this.state) {
            case WIND_UP:
                doWindUp();
                break;
            case CHARGING:
                doCharging();
                break;
            case RETREAT:
                doRetreat();
                break;
        }
    }

    private void doWindUp() {
        this.teddy.getMoveControl().setWantedPosition(
                this.teddy.getX(),
                this.teddy.getY(),
                this.teddy.getZ(),
                0.0
        );

        this.stateTicks++;
        if (this.stateTicks >= this.windUpDuration) {
            this.state = AttackState.CHARGING;
            this.stateTicks = 0;
        }
    }

    private void doCharging() {
        double distSqr = this.teddy.distanceToSqr(this.target);
        double hitRangeSqr = this.hitRange * this.hitRange;

        if (distSqr > hitRangeSqr) {
            this.teddy.getMoveControl().setWantedPosition(
                    this.target.getX(),
                    this.target.getY(),
                    this.target.getZ(),
                    this.chargeSpeed
            );
        } else {
            this.teddy.swing(InteractionHand.MAIN_HAND);
            this.teddy.doHurtTarget(this.target);
            this.state = AttackState.RETREAT;
            this.stateTicks = 0;
        }
    }

    private void doRetreat() {
        double dx = this.teddy.getX() - this.target.getX();
        double dy = this.teddy.getY() - this.target.getY();
        double dz = this.teddy.getZ() - this.target.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 0.1) {
            dist = 0.1;
        }

        double rx = this.teddy.getX() + (dx / dist) * this.retreatDistance;
        double ry = this.teddy.getY() + (dy / dist) * this.retreatDistance;
        double rz = this.teddy.getZ() + (dz / dist) * this.retreatDistance;

        this.teddy.getMoveControl().setWantedPosition(rx, ry, rz, this.retreatSpeed);

        this.stateTicks++;
        if (this.stateTicks >= this.retreatDuration) {
            this.state = AttackState.WIND_UP;
            this.stateTicks = 0;
        }
    }

}