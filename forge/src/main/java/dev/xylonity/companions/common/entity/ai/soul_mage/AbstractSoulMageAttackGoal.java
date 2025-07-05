package dev.xylonity.companions.common.entity.ai.soul_mage;

import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import dev.xylonity.knightlib.common.api.TickScheduler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Random;

public abstract class AbstractSoulMageAttackGoal extends Goal {
    protected final SoulMageEntity soulMage;
    protected int attackTicks;
    protected int nextUseTick;
    protected final int minCooldown;
    protected final int maxCooldown;
    protected boolean started;
    protected final String attackType;

    public AbstractSoulMageAttackGoal(SoulMageEntity soulMage, int minCd, int maxCd, String attackType) {
        this.soulMage = soulMage;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.attackType = attackType;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (soulMage.isAttacking()) return false;
        if (soulMage.getTarget() == null) return false;
        if (soulMage.tickCount < nextUseTick) return false;
        if (!hasRequiredBook()) return false;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return attackTicks < 25;
    }

    @Override
    public void start() {
        attackTicks = 0;
        soulMage.setAttacking(true);
        TickScheduler.scheduleBoth(soulMage.level(), () -> soulMage.setAttacking(false), 25);
        int rn = new Random().nextInt(3) + 1;
        soulMage.setAttackAnimationName(rn == 1 ? "spell" : rn == 2 ? "spell2" : "spell3");
        soulMage.setCurrentAttackType(attackType);
        started = true;
    }

    @Override
    public void stop() {
        started = false;
        soulMage.setCurrentAttackType("NONE");

        int randomCd = minCooldown + soulMage.getRandom().nextInt(maxCooldown - minCooldown + 1);
        nextUseTick = soulMage.tickCount + randomCd;
    }

    @Override
    public void tick() {
        if (!started) return;

        LivingEntity target = soulMage.getTarget();
        if (target != null) {
            soulMage.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        attackTicks++;

        if (target != null) this.soulMage.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (shouldPerformAttack(target)) {
            performAttack(target);
        }

        if (attackTicks >= 25) {
            stop();
        }

    }

    protected boolean shouldPerformAttack(LivingEntity target) {
        return attackTicks == 6 && target != null && target.isAlive();
    }

    protected abstract void performAttack(LivingEntity target);
    protected abstract boolean hasRequiredBook();

}