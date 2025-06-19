package dev.xylonity.companions.common.entity.ai.puppet;

import dev.xylonity.companions.common.entity.custom.PuppetEntity;
import dev.xylonity.knightlib.common.api.TickScheduler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Random;

public abstract class AbstractPuppetAttackGoal extends Goal {
    protected final PuppetEntity puppet;
    protected int attackTicks;
    protected int nextUseTick;
    protected final int minCooldown;
    protected final int maxCooldown;
    protected boolean started;
    protected final String attackType;

    public AbstractPuppetAttackGoal(PuppetEntity puppet, int minCd, int maxCd, String attackType) {
        this.puppet = puppet;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.attackType = attackType;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (puppet.isAttacking() != 0) return false;
        if (puppet.getTarget() == null) return false;
        if (puppet.tickCount < nextUseTick) return false;
        if (!hasRequiredArm()) return false;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return attackTicks < 13;
    }

    @Override
    public void start() {
        attackTicks = 0;

        if (puppet.getActiveArms() != 3) {
            if (puppet.getActiveArms() == 1) {
                puppet.setAttacking(1);
            } else if (puppet.getActiveArms() == 2) {
                puppet.setAttacking(2);
            }
        } else {
            puppet.setAttacking(new Random().nextInt(1, 3));
        }

        TickScheduler.scheduleBoth(puppet.level(), () -> puppet.setAttacking(0), 13);
        puppet.setAttackAnimationName(puppet.isAttacking() == 1 ? "attack_l" : "attack_r");
        puppet.setCurrentAttackType(attackType);
        started = true;
    }

    @Override
    public void stop() {
        started = false;
        puppet.setCurrentAttackType("NONE");

        int randomCd = minCooldown + puppet.getRandom().nextInt(maxCooldown - minCooldown + 1);
        nextUseTick = puppet.tickCount + randomCd;
    }

    @Override
    public void tick() {
        if (!started) return;

        LivingEntity target = puppet.getTarget();
        if (target != null) {
            puppet.getLookControl().setLookAt(target, 90.0F, 90.0F);
        }

        attackTicks++;

        if (target != null) this.puppet.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (shouldPerformAttack(target)) {
            performAttack(target);
        }

        if (attackTicks >= 13) {
            stop();
        }

    }

    protected boolean shouldPerformAttack(LivingEntity target) {
        return attackTicks == 3 && target != null && target.isAlive();
    }

    protected abstract void performAttack(LivingEntity target);
    protected abstract boolean hasRequiredArm();

}