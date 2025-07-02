package dev.xylonity.companions.common.entity.ai.cornelius.summon;

import dev.xylonity.companions.common.entity.CompanionSummonEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public abstract class AbstractCorneliusSummonAttackGoal extends Goal {
    protected final CompanionSummonEntity summon;
    protected final int attackDuration;
    protected final int minCooldown, maxCooldown;
    protected int attackTicks;
    protected int nextUseTick;
    protected boolean started;

    public AbstractCorneliusSummonAttackGoal(CompanionSummonEntity summon, int attackDuration, int minCd, int maxCd) {
        this.summon = summon;
        this.attackDuration = attackDuration;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.nextUseTick = -1;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (summon.getClass() != summonType()) return false;
        if (summon.getAttackType() != 0) return false;
        if (summon.getTarget() == null) return false;

        if (nextUseTick < 0) {
            nextUseTick = summon.tickCount + minCooldown + summon.getRandom().nextInt(maxCooldown - minCooldown + 1);
            return false;
        }

        return summon.tickCount >= nextUseTick;
    }

    @Override
    public boolean canContinueToUse() {
        return started && attackTicks < attackDuration;
    }

    @Override
    public void start() {
        attackTicks = 0;
        started = true;
        summon.setAttackType(getAttackType());
    }

    @Override
    public void stop() {
        started = false;
        summon.setAttackType(0);
        int cd = minCooldown + summon.getRandom().nextInt(maxCooldown - minCooldown + 1);
        nextUseTick = summon.tickCount + cd;
    }

    @Override
    public void tick() {
        LivingEntity target = summon.getTarget();
        if (target != null) {
            summon.getLookControl().setLookAt(target, 30F, 30F);
        }

        if (attackTicks == attackDelay() && target != null && target.isAlive()) {
            performAttack(target);
        }

        attackTicks++;
    }

    public static boolean isEntityInFront(LivingEntity viewer, Entity target, double fov) {
        Vec3 view = viewer.getLookAngle().normalize();
        Vec3 toTarget = target.position().add(0, target.getEyeHeight() * 0.5, 0).subtract(viewer.getEyePosition(1)).normalize();
        double angle = Math.acos(view.dot(toTarget)) * (180.0 / Math.PI);
        return angle < (fov / 2);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    protected abstract int getAttackType();
    protected abstract void performAttack(LivingEntity target);
    protected abstract int attackDelay();
    protected abstract Class<? extends CompanionSummonEntity> summonType();

}
