package dev.xylonity.companions.common.entity.ai.pontiff.goal;

import dev.xylonity.companions.common.entity.HostileEntity;
import dev.xylonity.companions.common.entity.ai.pontiff.AbstractSacredPontiffAttackGoal;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public class HolinessStakeAttackGoal extends AbstractSacredPontiffAttackGoal {

    public HolinessStakeAttackGoal(SacredPontiffEntity boss, int minCd, int maxCd) {
        super(boss, 25, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        pontiff.setNoMovement(true);
        pontiff.setShouldLookAtTarget(false);
    }

    @Override
    public void stop() {
        super.stop();
        pontiff.setNoMovement(false);
        pontiff.setShouldLookAtTarget(true);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && pontiff.getTarget() != null && pontiff.distanceTo(pontiff.getTarget()) < 8 && isEntityInFront(pontiff, pontiff.getTarget(), 160);
    }

    @Override
    public void tick() {
        super.tick();
        if (attackTicks == 5) {
            pontiff.playSound(CompanionsSounds.HOLINESS_STAKE.get());
        }
    }

    @Override
    protected int getAttackType() {
        return 2;
    }

    @Override
    protected void performAttack(LivingEntity target) {
        for (Entity e : target.level().getEntitiesOfClass(Entity.class,
                new AABB(new BlockPos((int) target.getX(), (int) target.getY(), (int) target.getZ())).inflate(3),
                e -> !(e instanceof HostileEntity) )) {
            if (e instanceof LivingEntity livingEntity && pontiff.hasLineOfSight(livingEntity) && isEntityInFront(pontiff, livingEntity, 100)) {
                pontiff.doHurtTarget(livingEntity);
                livingEntity.knockback(1f, pontiff.getX() - target.getX(), pontiff.getZ() - target.getZ());
            }
        }

    }

    @Override
    protected int attackDelay() {
        return 12;
    }

    @Override
    protected int attackState() {
        return 6;
    }

}