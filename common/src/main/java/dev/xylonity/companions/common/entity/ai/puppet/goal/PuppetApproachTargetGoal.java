package dev.xylonity.companions.common.entity.ai.puppet.goal;

import dev.xylonity.companions.common.entity.companion.PuppetEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

import java.util.EnumSet;

public class PuppetApproachTargetGoal extends Goal {
    private final PuppetEntity puppet;
    private LivingEntity target;
    private final PathNavigation navigation;
    private final double speedModifier;
    private final float startDistance;
    private final float stopDistance;
    private int timeToRecalcPath;

    public PuppetApproachTargetGoal(PuppetEntity teddy, double speedModifier, float startDistance, float stopDistance) {
        this.puppet = teddy;
        this.speedModifier = speedModifier;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.navigation = teddy.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = puppet.getTarget();
        if (puppet.getActiveArms() == 0 || puppet.getArmNames().equals("cannon,cannon")) return false;
        if (t == null || !t.isAlive()) return false;
        this.target = t;
        return puppet.distanceToSqr(this.target) > this.startDistance * this.startDistance;
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

        this.puppet.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            double distanceToTargetSq = this.puppet.distanceToSqr(this.target);

            if (distanceToTargetSq > this.startDistance * this.startDistance) {
                this.navigation.moveTo(this.target, this.speedModifier);
            } else if (distanceToTargetSq < this.stopDistance * this.stopDistance) {
                this.navigation.stop();
            }
        }
    }

}