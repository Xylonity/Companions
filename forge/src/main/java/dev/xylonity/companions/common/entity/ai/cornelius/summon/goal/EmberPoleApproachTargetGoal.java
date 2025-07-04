package dev.xylonity.companions.common.entity.ai.cornelius.summon.goal;

import dev.xylonity.companions.common.entity.custom.TeddyEntity;
import dev.xylonity.companions.common.entity.summon.EmberPoleEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

import java.util.EnumSet;

public class EmberPoleApproachTargetGoal extends Goal {
    private final EmberPoleEntity pole;
    private LivingEntity target;
    private final PathNavigation navigation;
    private final double speedModifier;
    private final float startDistance;
    private final float stopDistance;
    private int timeToRecalcPath;

    public EmberPoleApproachTargetGoal(EmberPoleEntity pole, double speedModifier, float startDistance, float stopDistance) {
        this.pole = pole;
        this.speedModifier = speedModifier;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.navigation = this.pole.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = pole.getTarget();
        if (t == null || !t.isAlive()) return false;
        this.target = t;
        return pole.distanceToSqr(this.target) > this.startDistance * this.startDistance;
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.target.isAlive() && !this.navigation.isDone();
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.target = null;
        this.navigation.stop();
    }

    @Override
    public void tick() {
        if (this.target == null) return;

        this.pole.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            double distanceToTargetSq = this.pole.distanceToSqr(this.target);

            if (distanceToTargetSq > this.startDistance * this.startDistance) {
                this.navigation.moveTo(this.target, this.speedModifier);
            } else if (distanceToTargetSq < this.stopDistance * this.stopDistance) {
                this.navigation.stop();
            }
        }
    }

}