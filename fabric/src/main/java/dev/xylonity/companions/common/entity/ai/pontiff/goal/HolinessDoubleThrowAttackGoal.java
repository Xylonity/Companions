package dev.xylonity.companions.common.entity.ai.pontiff.goal;

import dev.xylonity.companions.common.entity.ai.pontiff.AbstractSacredPontiffAttackGoal;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import dev.xylonity.companions.common.entity.projectile.HolinessNaginataProjectile;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class HolinessDoubleThrowAttackGoal extends AbstractSacredPontiffAttackGoal {

    public HolinessDoubleThrowAttackGoal(SacredPontiffEntity boss, int minCd, int maxCd) {
        super(boss, 85, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        pontiff.setNoMovement(true);
        pontiff.setShouldLookAtTarget(false);
        pontiff.playSound(CompanionsSounds.HOLINESS_FLY_OFF.get());
    }

    @Override
    public void stop() {
        super.stop();
        pontiff.setNoMovement(false);
        pontiff.setShouldLookAtTarget(true);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && pontiff.getTarget() != null && pontiff.distanceTo(pontiff.getTarget()) < 25 && isEntityInFront(pontiff, pontiff.getTarget(), 200);
    }

    @Override
    protected int getAttackType() {
        return 3;
    }

    @Override
    public void tick() {
        LivingEntity target = pontiff.getTarget();
        if (attackTicks == attackDelay() + 14 && target != null && target.isAlive()) {
            performSecondAttack(target);
        }

        super.tick();
    }

    @Override
    protected void performAttack(LivingEntity target) {
        Vec3 basePos = pontiff.position().add(0, pontiff.getEyeHeight() + 4, 0);
        Vec3 targetPos = target.position().add(0, target.getEyeHeight(), 0);
        Vec3 direction = targetPos.subtract(basePos).normalize();
        Vec3 perpen = new Vec3(-direction.z, 0, direction.x).normalize();
        spawnNaginata(basePos, perpen, -1.5f, targetPos, 2.8F + 0.5 * new Random().nextFloat());
    }

    protected void performSecondAttack(LivingEntity target) {
        Vec3 basePos = pontiff.position().add(0, pontiff.getEyeHeight() + 4, 0);
        Vec3 targetPos = target.position().add(0, target.getEyeHeight(), 0);
        Vec3 direction = targetPos.subtract(basePos).normalize();
        Vec3 perpen = new Vec3(-direction.z, 0, direction.x).normalize();
        spawnNaginata(basePos, perpen, 1.5f, targetPos, 4.8F + 0.5 * new Random().nextFloat());
    }

    private void spawnNaginata(Vec3 basePos, Vec3 perpen, float side, Vec3 targetPos, double speed) {
        Vec3 spawnPos = basePos.add(perpen.scale(side));
        Vec3 aimDir = targetPos.subtract(spawnPos).normalize();

        HolinessNaginataProjectile naginata = CompanionsEntities.HOLINESS_NAGINATA.create(pontiff.level());
        if (naginata != null) {
            naginata.setOwner(pontiff);
            naginata.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            naginata.setDeltaMovement(aimDir.scale(speed));
            naginata.refreshOrientation();
            naginata.setInvisible(true);
            pontiff.level().addFreshEntity(naginata);
        }
    }

    @Override
    protected int attackDelay() {
        return 40;
    }

    @Override
    protected int phase() {
        return 2;
    }

}