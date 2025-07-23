package dev.xylonity.companions.common.entity.ai.pontiff.goal;

import dev.xylonity.companions.common.entity.HostileEntity;
import dev.xylonity.companions.common.entity.ai.pontiff.AbstractSacredPontiffAttackGoal;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import dev.xylonity.companions.common.entity.projectile.trigger.FireRayBeamEntity;
import dev.xylonity.companions.registry.CompanionsSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class PontiffMeleeAttackGoal extends AbstractSacredPontiffAttackGoal {

    public PontiffMeleeAttackGoal(SacredPontiffEntity boss, int minCd, int maxCd) {
        super(boss, 31, minCd, maxCd);
    }

    @Override
    public void start() {
        super.start();
        pontiff.setNoMovement(true);
        pontiff.playSound(CompanionsSounds.PONTIFF_FRONT_ATTACK.get());
    }

    @Override
    public void stop() {
        super.stop();
        pontiff.setNoMovement(false);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && pontiff.getTarget() != null && pontiff.distanceTo(pontiff.getTarget()) < 3 && isEntityInFront(pontiff, pontiff.getTarget(), 200);
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
            if (e instanceof LivingEntity livingEntity && pontiff.hasLineOfSight(livingEntity) && isEntityInFront(pontiff, livingEntity, 180)) {
                pontiff.doHurtTarget(livingEntity);
                livingEntity.knockback(0.5f, pontiff.getX() - target.getX(), pontiff.getZ() - target.getZ());
            }
        }

    }

    @Override
    protected int attackDelay() {
        return 10;
    }

    @Override
    protected int attackState() {
        return 2;
    }

}