package dev.xylonity.companions.common.entity.ai.pontiff;

import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public abstract class AbstractSacredPontiffAttackGoal extends Goal {
    protected final SacredPontiffEntity pontiff;
    private final int attackDuration;
    private final int minCooldown, maxCooldown;
    protected int attackTicks;
    protected int nextUseTick;
    protected boolean started;

    public AbstractSacredPontiffAttackGoal(SacredPontiffEntity pontiff, int attackDuration, int minCd, int maxCd) {
        this.pontiff = pontiff;
        this.attackDuration = attackDuration;
        this.minCooldown = minCd;
        this.maxCooldown = maxCd;
        this.nextUseTick = -1;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!pontiff.shouldAttack()) return false;
        if (pontiff.getState() != attackState()) return false;
        if (pontiff.getAttackType() != 0) return false;
        if (pontiff.getTarget() == null) return false;

        if (nextUseTick < 0) {
            nextUseTick = pontiff.tickCount + minCooldown + pontiff.getRandom().nextInt(maxCooldown - minCooldown + 1);
            return false;
        }

        return pontiff.tickCount >= nextUseTick;
    }

    @Override
    public boolean canContinueToUse() {
        return started && attackTicks < attackDuration;
    }

    @Override
    public void start() {
        attackTicks = 0;
        started = true;
        pontiff.setAttackType(getAttackType());
    }

    @Override
    public void stop() {
        started = false;
        pontiff.setAttackType(0);
        int cd = minCooldown + pontiff.getRandom().nextInt(maxCooldown - minCooldown + 1);
        nextUseTick = pontiff.tickCount + cd;
    }

    @Override
    public void tick() {
        LivingEntity target = pontiff.getTarget();
        if (target != null) {
            pontiff.getLookControl().setLookAt(target, 30F, 30F);
        }

        if (attackTicks == attackDelay() && target != null && target.isAlive()) {
            performAttack(target);
        }

        attackTicks++;
    }

    public static boolean isEntityInFront(LivingEntity viewer, Entity target, double fov) {
        Vec3 view = viewer.getLookAngle().normalize();
        Vec3 toTarget = new Vec3(target.getX(), viewer.getY(), target.getZ()).subtract(viewer.position()).normalize();
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
    protected abstract int attackState();

}
