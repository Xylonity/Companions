package dev.xylonity.companions.common.entity.ai.teddy;

import dev.xylonity.companions.common.entity.custom.TeddyEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class TeddyAttackGoal extends Goal {
    private final TeddyEntity teddy;
    private final double speedModifier;
    private final double attackRange;
    private final int attackCooldown;

    private int attackTime;

    public TeddyAttackGoal(TeddyEntity teddy, double speedModifier, double attackRange, int attackCooldown) {
        this.teddy = teddy;
        this.speedModifier = speedModifier;
        this.attackRange = attackRange;
        this.attackCooldown = attackCooldown;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.teddy.getPhase() != 2) {
            return false;
        }

        LivingEntity target = this.teddy.getTarget();
        if (target == null) {
            return false;
        }

        return target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.teddy.getPhase() != 2) {
            return false;
        }
        LivingEntity target = this.teddy.getTarget();
        return (target != null && target.isAlive());
    }

    @Override
    public void start() {
        this.teddy.noPhysics = false;
        this.teddy.setNoGravity(true);

        this.attackTime = 0;
    }

    @Override
    public void stop() {
        this.teddy.getMoveControl().setWantedPosition(this.teddy.getX(), this.teddy.getY(), this.teddy.getZ(), 0.0);
    }

    @Override
    public void tick() {
        LivingEntity target = this.teddy.getTarget();
        if (target == null) {
            return;
        }

        if (this.attackTime > 0) {
            this.attackTime--;
        }

        this.teddy.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double distSqr = this.teddy.distanceToSqr(target);
        double rangeSqr = this.attackRange * this.attackRange;

        if (distSqr > rangeSqr) {
            this.teddy.getMoveControl().setWantedPosition(
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    this.speedModifier
            );
        } else {
            this.teddy.getMoveControl().setWantedPosition(this.teddy.getX(), this.teddy.getY(), this.teddy.getZ(), 0.0);

            if (this.attackTime <= 0) {
                this.teddy.doHurtTarget(target);
                this.attackTime = this.attackCooldown;
            }
        }
    }
}